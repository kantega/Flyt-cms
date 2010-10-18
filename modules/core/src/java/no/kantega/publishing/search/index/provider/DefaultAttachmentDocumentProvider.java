/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.search.index.provider;

import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.commons.media.MimeTypes;
import no.kantega.publishing.common.ao.AttachmentAO;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.data.enums.AssociationType;
import no.kantega.publishing.common.util.InputStreamHandler;
import no.kantega.publishing.common.service.impl.PathWorker;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.search.dao.AksessDao;
import no.kantega.publishing.search.extraction.TextExtractor;
import no.kantega.publishing.search.extraction.TextExtractorSelector;
import no.kantega.search.index.Fields;
import no.kantega.search.index.provider.DocumentProvider;
import no.kantega.search.index.provider.DocumentProviderHandler;
import no.kantega.search.index.rebuild.ProgressReporter;
import no.kantega.search.result.SearchHit;
import no.kantega.search.result.SearchHitContext;
import no.kantega.publishing.search.model.AksessSearchHit;
import no.kantega.publishing.search.model.AksessSearchHitContext;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DefaultAttachmentDocumentProvider implements DocumentProvider {
    private AksessDao aksessDao;
    private List boosters = new ArrayList();
    private TextExtractorSelector textExtractorSelector;
    private int docCount = -1;
    private static final String SOURCE = "aksess.DefaultAttachmentDocumentProvider";
    private String sourceId;

    public String getSourceId() {
        return sourceId;
    }

    public String getDocumentType() {
        return Fields.TYPE_ATTACHMENT;
    }

    public AksessSearchHit createSearchHit() {
        return new AksessSearchHit();
    }

    public void processSearchHit(SearchHit sHit, SearchHitContext context, Document doc) {
        AksessSearchHit searchHit = (AksessSearchHit)sHit;
        searchHit.setTitle(doc.get(Fields.TITLE));
        int attachmentId = Integer.parseInt(doc.get(Fields.ATTACHMENT_ID));
        if (attachmentId != -1) {
            String docIdString = doc.get(Fields.ATTACHMENT_CONTENT_ID_REF);
            if(docIdString != null) {
                int docId = Integer.parseInt(docIdString);
                ContentIdentifier cid = new ContentIdentifier();
                if (context != null && context instanceof AksessSearchHitContext) {
                    cid.setSiteId(((AksessSearchHitContext)context).getSiteId());
                }
                cid.setContentId(docId);
                try {
                    List<PathEntry> path = PathWorker.getPathByContentId(cid);
                    searchHit.setPathElements(path);
                } catch (SystemException e) {
                    Log.error(SOURCE, e, null, null);
                }
            }

            String fileName = doc.get(Fields.ATTACHMENT_FILE_NAME);
            if (fileName != null) {
                fileName = fileName.toLowerCase();
                if (fileName.indexOf(".") != -1) {
                    String fileExtension = fileName.substring(fileName.lastIndexOf("."), fileName.length());
                    fileName = fileName.substring(0,fileName.lastIndexOf("."));
                    searchHit.setFileExtension(fileExtension);
                    searchHit.setFileName(fileName);
                }
                searchHit.setMimeType(MimeTypes.getMimeType(searchHit.getFileName() + "." + searchHit.getFileExtension()));
            }
            
            if (searchHit.getTitle() == null || searchHit.getTitle().length() == 0) {
                searchHit.setTitle(doc.get(Fields.ATTACHMENT_FILE_NAME));
            }

            try {
                searchHit.setFileSize(Integer.parseInt(doc.get(Fields.ATTACHMENT_FILE_SIZE)));
            } catch (NumberFormatException e) {
                Log.error(SOURCE, "NumberFormatException occured while trying to parse filesize from index for attachment:"+searchHit.getId(), null, null);
            }

            searchHit.setUrl(Aksess.getContextPath() + "/attachment.ap?id=" + attachmentId);
        }
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public void provideDocuments(DocumentProviderHandler handler, ProgressReporter reporter) {
        try {
            int i = -1;
            int c=0;
            i = aksessDao.getNextActiveAttachmentId(i);
            int total = aksessDao.countActiveAttachmentIds();
            while((i = aksessDao.getNextActiveAttachmentId(i)) > 0) {
                if(handler.isStopRequested()) {
                    Log.info(SOURCE, "provideDocuments returning since were told to after " + c + " attachments", null, null);
                    break;
                }
                try {
                        Attachment a = AttachmentAO.getAttachment(i);
                        Document d = getAttachmentDocument(a);
                        handler.handleDocument(d);
                        reporter.reportProgress(++c, "aksess-vedlegg", total);

                } catch (Throwable e) {
                        Log.error(SOURCE, "Caught throwable during indexing of attachment " +i, null, null);
                        Log.error(SOURCE, e, null, null);
                }
            }
        } catch (SQLException e) {
            Log.error(SOURCE, "Exception getting next attachment, index rebuild failed", null, null);
            e.printStackTrace();
        }
    }

    public Document provideDocument(String id) {
        try {
            int i = Integer.parseInt(id);
            return getAttachmentDocument(AttachmentAO.getAttachment(i));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (SystemException e) {
            
        }
        return null;
    }

    public Term getDeleteTerm(String id) {
        return new Term(Fields.ATTACHMENT_ID, id);
    }

    public Term getDeleteAllTerm() {
        return new Term(Fields.DOCTYPE, Fields.TYPE_ATTACHMENT);
    }

    public void setAksessDao(AksessDao aksessDao) {
        this.aksessDao = aksessDao;
    }


    private Document getAttachmentDocument(Attachment a) throws SQLException, SystemException {
        Content content = ContentAO.getContent(new ContentIdentifier(a.getContentId()), false);
        if (!content.isSearchable()) {
            return null;
        }

        TextExtractor te = textExtractorSelector.select(a.getFilename());
        Document d = new Document();

        Field fTitle = new Field(Fields.TITLE, content.getTitle(), Field.Store.YES, Field.Index.ANALYZED);
        fTitle.setBoost(2);
        d.add(fTitle);

        Field fAltTitle = new Field(Fields.ALT_TITLE, content.getAltTitle(), Field.Store.YES, Field.Index.ANALYZED);
        fAltTitle.setBoost(2);
        d.add(fAltTitle);

        String alias = content.getAlias();
        if (alias != null && alias.length() > 1) {
            alias = alias.replaceAll("/", " ");
            Field fAlias = new Field(Fields.ALIAS, alias, Field.Store.YES, Field.Index.ANALYZED);
            fAlias.setBoost(2);
            d.add(fAlias);
        }

        Field fKeywords = new Field(Fields.KEYWORDS, content.getKeywords() == null ? "" : content.getKeywords(), Field.Store.NO, Field.Index.ANALYZED);
        fKeywords.setBoost(1.5f);
        d.add(fKeywords);                    

        d.add(new Field(Fields.TITLE_UNANALYZED, content.getTitle(), Field.Store.NO, Field.Index.NOT_ANALYZED));
        d.add(new Field(Fields.DOCTYPE, Fields.TYPE_ATTACHMENT, Field.Store.YES, Field.Index.NOT_ANALYZED));
        d.add(new Field(Fields.ATTACHMENT_ID, Integer.toString(a.getId()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        d.add(new Field(Fields.ATTACHMENT_CONTENT_ID_REF, Integer.toString(a.getContentId()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        d.add(new Field(Fields.SITE_ID, getSiteId(content), Field.Store.YES, Field.Index.NOT_ANALYZED));
        d.add(new Field(Fields.CATEGORY, getCategory(content), Field.Store.YES, Field.Index.NOT_ANALYZED));
        d.add(new Field(Fields.ATTACHMENT_FILE_NAME, a.getFilename(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        d.add(new Field(Fields.LANGUAGE,  Integer.toString(content.getLanguage()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        d.add(new Field(Fields.FILE_TYPE, getSuffix(a.getFilename()).toLowerCase(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        d.add(new Field(Fields.LAST_MODIFIED, DateTools.dateToString(a.getLastModified(), DateTools.Resolution.MINUTE), Field.Store.YES, Field.Index.NOT_ANALYZED));
        d.add(new Field(Fields.CONTENT_STATUS, Integer.toString(content.getStatus()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        d.add(new Field(Fields.CONTENT_VISIBILITY_STATUS, Integer.toString(content.getVisibilityStatus()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        d.add(new Field(Fields.CONTENT_PARENTS, getParents(content), Field.Store.YES, Field.Index.ANALYZED));
        d.add(new Field(Fields.DOCUMENT_TYPE_ID, Integer.toString(content.getDocumentTypeId()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        d.add(new Field(Fields.ATTACHMENT_FILE_SIZE, Integer.toString(a.getSize()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        

        String text = "";
        if (te != null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            AttachmentAO.streamAttachmentData(a.getId(), new InputStreamHandler(bos));

            text = te.extractText(new ByteArrayInputStream(bos.toByteArray()));
            d.add(new Field(Fields.CONTENT, text, Field.Store.NO, Field.Index.ANALYZED));
            String summary = text.substring(0, (text.length() > Fields.SUMMARY_LENGTH) ? Fields.SUMMARY_LENGTH : text.length())  + (text.length() > Fields.SUMMARY_LENGTH  ? "..." : "");
            d.add(new Field(Fields.SUMMARY, summary, Field.Store.YES, Field.Index.NOT_ANALYZED));
        }
        return d;
    }

    private String getSiteId(Content content) {
        StringBuffer siteId = new StringBuffer();
        List associations = content.getAssociations();
        for (int i = 0; i < associations.size(); i++) {
            Association a = (Association)associations.get(i);
            if (a.getAssociationtype() == AssociationType.DEFAULT_POSTING_FOR_SITE) {
                if (siteId.length() > 0) siteId.append(" ");
                siteId.append(a.getSiteId());
            }
        }
        return siteId.toString();
    }

    private String getCategory(Content content) {
        StringBuffer category = new StringBuffer();
        List associations = content.getAssociations();
        for (int i = 0; i < associations.size(); i++) {
            Association a = (Association)associations.get(i);
            if (category.length() > 0) category.append(" ");
            category.append(a.getCategory().getId());
        }
        return category.toString();
    }

    private String getParents(Content content) {
        StringBuffer parents = new StringBuffer();
        List associations = content.getAssociations();
        for (int i = 0; i < associations.size(); i++) {
            Association a = (Association)associations.get(i);
            if (parents.length() > 0) parents.append(" ");
            String path = a.getPath();
            path = path.replace('/', ' ');
            parents.append(path);
        }
        return parents.toString();
    }

    public void setBoosters(List boosters) {
        this.boosters = boosters;
    }

    public void setTextExtractorSelector(TextExtractorSelector textExtractorSelector) {
        this.textExtractorSelector = textExtractorSelector;
    }

    private String getSuffix(String filename) {

        if(filename != null && filename.indexOf(".") >= 0) {
            return filename.substring(filename.lastIndexOf(".") +(filename.endsWith(".") ? 0 : 1));
        } else {
            return "";
        }

    }
}
