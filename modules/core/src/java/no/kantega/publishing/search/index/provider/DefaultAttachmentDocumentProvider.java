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
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.AttachmentAO;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.data.enums.AssociationType;
import no.kantega.publishing.common.data.enums.ContentType;
import no.kantega.publishing.common.service.impl.PathWorker;
import no.kantega.publishing.common.util.Counter;
import no.kantega.publishing.common.util.InputStreamHandler;
import no.kantega.publishing.search.dao.AksessDao;
import no.kantega.publishing.search.extraction.TextExtractor;
import no.kantega.publishing.search.extraction.TextExtractorSelector;
import no.kantega.publishing.search.index.jobs.RebuildIndexJob;
import no.kantega.publishing.search.model.AksessSearchHit;
import no.kantega.publishing.search.model.AksessSearchHitContext;
import no.kantega.search.index.Fields;
import no.kantega.search.index.provider.DocumentProvider;
import no.kantega.search.index.provider.DocumentProviderHandler;
import no.kantega.search.index.rebuild.ProgressReporter;
import no.kantega.search.result.QueryInfo;
import no.kantega.search.result.SearchHit;
import no.kantega.search.result.SearchHitContext;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.highlight.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.common.collect.Lists.partition;

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
                if (fileName.contains(".")) {
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

            searchHit.setAllText(doc.get(Fields.CONTENT));
            if (StringUtils.isNotEmpty(searchHit.getAllText())) {
                QueryInfo queryInfo = context.getQueryInfo();
                if (queryInfo != null) {
                    // Add text to show where hit was found
                    Formatter formatter = new SimpleHTMLFormatter("<span class=\"highlight\">", "</span>");
                    Scorer scorer = new QueryScorer(queryInfo.getQuery());
                    Highlighter highlighter = new Highlighter(formatter, scorer);
                    try {
                        String frag = highlighter.getBestFragment(queryInfo.getAnalyzer(), Fields.CONTENT, searchHit.getAllText());
                        searchHit.setContextText(frag);
                    } catch (IOException e) {
                        Log.error(SOURCE, e, null, null);
                    }  catch (InvalidTokenOffsetsException e) {
                        Log.error(SOURCE, e, null, null);
                    }
                }
            }

            try {
                searchHit.setFileSize(Integer.parseInt(doc.get(Fields.ATTACHMENT_FILE_SIZE)));
            } catch (NumberFormatException e) {
                Log.error(SOURCE, "NumberFormatException occured while trying to parse filesize from index for attachment:"+searchHit.getId(), null, null);
            }

            searchHit.setUrl(Aksess.getContextPath() + "/" + Aksess.ATTACHMENT_REQUEST_HANDLER +"?id=" + attachmentId);
        }
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public void provideDocuments(DocumentProviderHandler handler, ProgressReporter reporter) {
        provideDocuments(handler, reporter, Collections.emptyMap());
    }

    private class worker implements Runnable {

        private DocumentProviderHandler handler;
        private ProgressReporter reporter;
        private Counter counter;
        private CyclicBarrier cyclicBarrier;
        private List<Integer> identifiers;
        private int totalNumberAttachments;

        public worker(DocumentProviderHandler handler, ProgressReporter reporter, Counter c, CyclicBarrier cyclicBarrier, List<Integer> identifiers, int size) {
            this.handler = handler;
            this.reporter = reporter;
            counter = c;
            this.cyclicBarrier = cyclicBarrier;
            this.identifiers = identifiers;
            totalNumberAttachments = size;
        }

        public void run() {
            for (Integer identifier : identifiers) {
                if(handler.isStopRequested()) {
                    Log.info(SOURCE, "provideDocuments returning since stop where requested", null, null);
                    return;
                }

                try {
                    Attachment a = AttachmentAO.getAttachment(identifier);
                    Document d = getAttachmentDocument(a);
                    if (d != null) {
                        handler.handleDocument(d);
                    }
                    counter.increment();
                    reporter.reportProgress(counter.getI(), "aksess-vedlegg", totalNumberAttachments);

                } catch (Throwable e) {
                    Log.error(SOURCE, "Caught throwable during indexing of attachment " +identifier, null, null);
                    Log.error(SOURCE, e, null, null);
                }
            }
            try {
                Log.info(SOURCE, "Thread done handling content");
                cyclicBarrier.await();
            } catch (InterruptedException e) {
                Log.error("Worker interupted", e);
            } catch (BrokenBarrierException e) {
                Log.error("Barrier error", e);
            }
        }
    }

    public void provideDocuments(DocumentProviderHandler handler, ProgressReporter reporter, Map options) {
        final Counter c = new Counter();

        Integer numberOfConcurrentHandlers = 1;
        if(options.containsKey(RebuildIndexJob.NUMBEROFCONCURRENTHANDLERS)){
            numberOfConcurrentHandlers = (Integer) options.get(RebuildIndexJob.NUMBEROFCONCURRENTHANDLERS);
        }

        try {
            List<Integer> attachmentIds = aksessDao.getAttachmentIds();
            int partitionSize = (attachmentIds.size() / numberOfConcurrentHandlers) + 1;
            List<List<Integer>> attachmentIdsPartition = partition(attachmentIds, partitionSize);
            CyclicBarrier cyclicBarrier = new CyclicBarrier(attachmentIds.size() + 1);

            Log.info(SOURCE, "Starting provideDocuments, number of concurrent handlers: " + numberOfConcurrentHandlers);
            ExecutorService pool = Executors.newFixedThreadPool(numberOfConcurrentHandlers);

            for(List<Integer> identifiers : attachmentIdsPartition){
                pool.submit(new worker(handler, reporter, c, cyclicBarrier, identifiers, attachmentIds.size()));
            }
            cyclicBarrier.await();
            Log.info(SOURCE, "All threads done executing handlers");
        } catch (SQLException e) {
            Log.debug(SOURCE, e.getMessage());
        } catch (InterruptedException e) {
            Log.debug(SOURCE, e.getMessage());
        } catch (BrokenBarrierException e) {
            Log.debug(SOURCE, e.getMessage());
        }
    }

    public Document provideDocument(String id) {
        try {
            int i = Integer.parseInt(id);
            return getAttachmentDocument(AttachmentAO.getAttachment(i));
        } catch (SQLException e) {
            Log.error(SOURCE, e);
        } catch (SystemException e) {
            Log.error(SOURCE, e);
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

        if (content.getType() == ContentType.FILE) {
            // Text in files are indexed as part of content object, should not index here to get duplicate
            return null;
        }

        TextExtractor te = textExtractorSelector.select(a.getFilename());
        if (te == null) {
            return null;
        }

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
        if (content.getDocumentTypeId() > 0) {
            d.add(new Field(Fields.DOCUMENT_TYPE_ID, Integer.toString(content.getDocumentTypeId()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        }
        d.add(new Field(Fields.ATTACHMENT_FILE_SIZE, Integer.toString(a.getSize()), Field.Store.YES, Field.Index.NOT_ANALYZED));
        d.add(new Field(Fields.URL, a.getUrl(), Field.Store.YES, Field.Index.NOT_ANALYZED));

        String text = "";
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        AttachmentAO.streamAttachmentData(a.getId(), new InputStreamHandler(bos));

        text = te.extractText(new ByteArrayInputStream(bos.toByteArray()), a.getFilename());
        d.add(new Field(Fields.CONTENT, text, Field.Store.YES, Field.Index.ANALYZED));
        String summary = text.substring(0, (text.length() > Fields.SUMMARY_LENGTH) ? Fields.SUMMARY_LENGTH : text.length())  + (text.length() > Fields.SUMMARY_LENGTH  ? "..." : "");
        d.add(new Field(Fields.SUMMARY, summary, Field.Store.YES, Field.Index.NOT_ANALYZED));
        return d;
    }

    private String getSiteId(Content content) {
        StringBuilder siteId = new StringBuilder();
        List<Association> associations = content.getAssociations();
        for (Association a : associations) {
            if (a.getAssociationtype() == AssociationType.DEFAULT_POSTING_FOR_SITE) {
                if (siteId.length() > 0) siteId.append(" ");
                siteId.append(a.getSiteId());
            }
        }
        return siteId.toString();
    }

    private String getCategory(Content content) {
        StringBuilder category = new StringBuilder();
        List<Association> associations = content.getAssociations();
        for (Association a : associations) {
            if (category.length() > 0) category.append(" ");
            category.append(a.getCategory().getId());
        }
        return category.toString();
    }

    private String getParents(Content content) {
        StringBuilder parents = new StringBuilder();
        List<Association> associations = content.getAssociations();
        for (Association a : associations) {
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

        if(filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf(".") +(filename.endsWith(".") ? 0 : 1));
        } else {
            return "";
        }

    }
}
