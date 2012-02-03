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

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.AttachmentAO;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.common.ao.ContentHandler;
import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.AttributeHandler;
import no.kantega.publishing.common.data.enums.AssociationType;
import no.kantega.publishing.common.data.enums.AttributeDataType;
import no.kantega.publishing.common.data.enums.ContentType;
import no.kantega.publishing.common.service.impl.PathWorker;
import no.kantega.publishing.common.util.Counter;
import no.kantega.publishing.common.util.InputStreamHandler;
import no.kantega.publishing.search.SearchField;
import no.kantega.publishing.search.dao.AksessDao;
import no.kantega.publishing.search.extraction.TextExtractor;
import no.kantega.publishing.search.extraction.TextExtractorSelector;
import no.kantega.publishing.search.index.boost.ContentBooster;
import no.kantega.publishing.search.index.jobs.RebuildIndexJob;
import no.kantega.publishing.search.index.model.TmBaseName;
import no.kantega.publishing.search.model.AksessSearchHit;
import no.kantega.publishing.search.model.AksessSearchHitContext;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.enums.Privilege;
import no.kantega.search.index.Fields;
import no.kantega.search.index.provider.DocumentProvider;
import no.kantega.search.index.provider.DocumentProviderHandler;
import no.kantega.search.index.rebuild.ProgressReporter;
import no.kantega.search.result.QueryInfo;
import no.kantega.search.result.SearchHit;
import no.kantega.search.result.SearchHitContext;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.highlight.*;
import org.cyberneko.html.parsers.SAXParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class DefaultDocumentProvider implements DocumentProvider {

    private AksessDao aksessDao;
    private List<ContentBooster> boosters = new ArrayList<ContentBooster>();
    private int docCount = -1;
    private final String SOURCE = "aksess.DefaultDocumentProvider";
    private String sourceId;
    private List<SearchField> customSearchFields;

    private TextExtractorSelector textExtractorSelector;

    /**
     * {@inheritDoc}
     */
    public String getSourceId() {
        return sourceId;
    }

    /**
     * {@inheritDoc}
     */
    public AksessSearchHit createSearchHit() {
        return new AksessSearchHit();
    }

    /**
     * {@inheritDoc}
     */
    public void processSearchHit(SearchHit sHit, SearchHitContext context, Document doc) throws NotAuthorizedException {
        AksessSearchHitContext aContext = null;
        if (context instanceof AksessSearchHitContext) {
            aContext = (AksessSearchHitContext)context;
        }
        AksessSearchHit searchHit = (AksessSearchHit)sHit;
        searchHit.setDocument(doc);
        searchHit.setTitle(doc.get(Fields.TITLE));
        int contentId = Integer.parseInt(doc.get(Fields.CONTENT_ID));
        ContentIdentifier cid = new ContentIdentifier();
        if (context != null && aContext != null) {
            cid.setSiteId(aContext.getSiteId());
        }
        cid.setContentId(contentId);
        try {
            if (aContext != null && aContext.isShouldGetContentObject()) {
                addValuesFromContentDocument(context, aContext, searchHit, cid);

                List<PathEntry> path = PathWorker.getPathByContentId(cid);
                searchHit.setPathElements(path);
                searchHit.setUrl(Aksess.getContextPath() + "/content.ap?thisId=" + cid.getAssociationId());
            } else {
                searchHit.setUrl(Aksess.getContextPath() + "/content.ap?contentId=" + contentId);
            }

        } catch (SystemException e) {
            e.printStackTrace();
        }
    }

    private void addValuesFromContentDocument(SearchHitContext context, AksessSearchHitContext aContext, AksessSearchHit searchHit, ContentIdentifier cid) throws SystemException, NotAuthorizedException {
        Content c = ContentAO.getContent(cid, true);
        SecuritySession session = aContext != null ? aContext.getSecuritySession() : null;
        if (session != null) {
            if (!session.isAuthorized(c, Privilege.VIEW_CONTENT)) {
                throw new NotAuthorizedException("", SOURCE);
            }
        }

        if (c != null) {
            searchHit.setAllText(getArticleText(c));

            QueryInfo queryInfo = context.getQueryInfo();
            if (searchHit.getAllText().length() > 0 && queryInfo != null) {
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

            if (c.isOpenInNewWindow() || Aksess.doOpenLinksInNewWindow() && c.isExternalLink()) {
                searchHit.setDoOpenInNewWindow(true);
            }

            searchHit.setId(c.getAssociation().getAssociationId());

            searchHit.setContentObject(c);
        }
    }

    public String getDocumentType() {
        return Fields.TYPE_CONTENT;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public void setCustomSearchFields(List<SearchField> customSearchFields) {
        this.customSearchFields = customSearchFields;
    }

    public void provideDocuments(final DocumentProviderHandler handler, final ProgressReporter reporter) {
        provideDocuments(handler, reporter, Collections.emptyMap());
    }

    public void provideDocuments(final DocumentProviderHandler handler, final ProgressReporter reporter, Map options) {
        final Counter c = new Counter();

        Integer numberOfConcurrentHandlers = 1;
        if(options.containsKey(RebuildIndexJob.NUMBEROFCONCURRENTHANDLERS)){
            numberOfConcurrentHandlers = (Integer) options.get(RebuildIndexJob.NUMBEROFCONCURRENTHANDLERS);
        }
        
        ContentAO.forAllContentObjects(new ContentHandler() {

                    public void handleContent(Content content) {

                        try {
                            Document d = getContentDocument(content);
                            if(d != null) {
                                float boost = calculateBoost(content);
                                d.setBoost(boost);
                                handler.handleDocument(d);

                                c.increment();
                                reporter.reportProgress(c.getI(), "aksess-document", getTotalDocumentCount());
                            }
                        } catch (Throwable e) {
                            Log.error(SOURCE, "Caught throwable during indexing of document #" +c.getI() +" (id: " +content.getId() +")", null, null);
                            Log.error(SOURCE, e, null, null);
                        }

                    }

                }, new ContentAO.ContentHandlerStopper() {

            public boolean isStopRequested() {
                return handler.isStopRequested();
            }

        }, numberOfConcurrentHandlers);    
    }

    public Document provideDocument(String id) {
        try {
            int i = Integer.parseInt(id);
            Content content = ContentAO.getContent(new ContentIdentifier(i), false);
            if (content == null) {
                Log.info(SOURCE, "Content document not found, id:" + id, null, null);
                return null;
            }
            Document d = getContentDocument(content);
            if(d != null) {
                d.setBoost(calculateBoost(ContentAO.getContent(new ContentIdentifier(i), false)));
            }
            return d;

        } catch (Exception e) {
            Log.error(SOURCE, e);
        }
        return null;
    }

    public Term getDeleteTerm(String id) {
        return new Term(Fields.CONTENT_ID, id);
    }

    public Term getDeleteAllTerm() {
        return new Term(Fields.DOCTYPE, Fields.TYPE_CONTENT);
    }

    private int getTotalDocumentCount() {
        if(docCount < 0) {
            try {
                docCount = aksessDao.countActiveContentIds();
            } catch (SQLException e) {
                Log.error(SOURCE, e, null, null);
            }
        }
        return docCount;
    }

    private Document getContentDocument(Content content) throws SQLException {

        if (!content.isSearchable()) {
            return null;
        }

        Document d = null;

        try {
            d = new Document();

            String allText = "";
            allText += content.getTitle() + " ";

            if(content.getKeywords() != null && content.getKeywords().length() > 0) {
                allText += content.getKeywords() + " ";
            }

            if (content.getAltTitle() != null && content.getAltTitle().length() > 0) {
                allText += content.getAltTitle() + " ";
            }

            String title = content.getTitle();
            String siteId = getSiteId(content);
            String category = getCategory(content);

            d.add(new Field(Fields.DOCTYPE, Fields.TYPE_CONTENT, Field.Store.YES, Field.Index.NOT_ANALYZED));
            d.add(new Field(Fields.CONTENT_ID, Integer.toString(content.getId()), Field.Store.YES, Field.Index.NOT_ANALYZED));

            Field fTitle = new Field(Fields.TITLE, title, Field.Store.YES, Field.Index.ANALYZED);
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
                allText = allText + alias + " ";
            }

            allText += getArticleText(content);

            if (content.getType() == ContentType.FILE) {
                allText += getAttachmentText(content);
            }



            d.add(new Field(Fields.TITLE_UNANALYZED, title, Field.Store.NO, Field.Index.NOT_ANALYZED));
            d.add(new Field(Fields.SITE_ID, siteId, Field.Store.YES, Field.Index.ANALYZED));
            d.add(new Field(Fields.CONTENT, allText, Field.Store.NO, Field.Index.ANALYZED));
            d.add(new Field(Fields.CONTENT_UNSTEMMED, allText, Field.Store.NO, Field.Index.ANALYZED, Field.TermVector.YES));

            Field fKeywords = new Field(Fields.KEYWORDS, content.getKeywords() == null ? "" : content.getKeywords(), Field.Store.NO, Field.Index.ANALYZED);
            fKeywords.setBoost(1.5f);
            d.add(fKeywords);

            d.add(new Field(Fields.LAST_MODIFIED, DateTools.dateToString(content.getLastModified(), DateTools.Resolution.MINUTE), Field.Store.YES, Field.Index.NOT_ANALYZED));
            d.add(new Field(Fields.LANGUAGE, Integer.toString(content.getLanguage()), Field.Store.YES, Field.Index.NOT_ANALYZED));
            d.add(new Field(Fields.CATEGORY, category, Field.Store.YES, Field.Index.ANALYZED));
            String desc = stripHtml(content.getDescription());
            desc = desc == null  ? "" : desc;
            desc = desc.substring(0, (desc.length() > Fields.SUMMARY_LENGTH) ? Fields.SUMMARY_LENGTH : desc.length()) + (desc.length() > Fields.SUMMARY_LENGTH ? ".." : "");
            d.add(new Field(Fields.SUMMARY, desc, Field.Store.YES, Field.Index.NOT_ANALYZED));

            Field fTopics = new Field(Fields.TM_TOPICS, getTopics(content.getId()), Field.Store.YES, Field.Index.ANALYZED);
            fTopics.setBoost(1.5f);
            d.add(fTopics);


            d.add(new Field(Fields.CONTENT_PARENTS, getParents(content), Field.Store.YES, Field.Index.ANALYZED));
            d.add(new Field(Fields.CONTENT_TEMPLATE_ID, Integer.toString(content.getContentTemplateId()), Field.Store.NO, Field.Index.NOT_ANALYZED));
            d.add(new Field(Fields.CONTENT_STATUS, Integer.toString(content.getStatus()), Field.Store.YES, Field.Index.NOT_ANALYZED));
            d.add(new Field(Fields.CONTENT_VISIBILITY_STATUS, Integer.toString(content.getVisibilityStatus()), Field.Store.YES, Field.Index.NOT_ANALYZED));
            if (content.getDocumentTypeId() > 0) {
                d.add(new Field(Fields.DOCUMENT_TYPE_ID, Integer.toString(content.getDocumentTypeId()), Field.Store.YES, Field.Index.NOT_ANALYZED));
            }
            d.add(new Field(Fields.ASSOCIATION_ID, getAssociations(content), Field.Store.YES, Field.Index.ANALYZED));

            addAttributeFields(content, d);
            addOtherFields(content, d);

            if (customSearchFields != null) {
                for (SearchField searchField : customSearchFields) {
                    searchField.addToIndex(content, d);
                }
            }

            return d;
        } catch(Throwable e) {
            Log.error(getClass().getName(), "Exception creating index document for content id " + content.getId() +": " + e.getMessage(), null, null);
            Log.error(getClass().getName(), e, null, null);
            e.printStackTrace();
            return null;
        }
    }

    private String getAttachmentText(Content content) {
        String text = "";

        try {
            int attachmentId = Integer.parseInt(content.getLocation());

            Attachment attachment = AttachmentAO.getAttachment(attachmentId);

            TextExtractor te = textExtractorSelector.select(attachment.getFilename());
            if (te != null) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                AttachmentAO.streamAttachmentData(attachment.getId(), new InputStreamHandler(bos));
                text = te.extractText(new ByteArrayInputStream(bos.toByteArray()), attachment.getFilename());
            }

        } catch (Exception e) {
            Log.error(getClass().getName(), e);
        }

        return text;
    }

    protected void addOtherFields(Content content, Document d) {
        // Default implementasjon er tom - kan overrides for ? legge til egendefinerte felt.
    }

    protected AksessDao getAksessDao() {
        return aksessDao;
    }

    private void addAttributeFields(Content content, final Document d) {
        content.doForEachAttribute(AttributeDataType.CONTENT_DATA, new AttributeHandler() {
            public void handleAttribute(Attribute attribute) {
                attribute.addIndexFields(d);
            }
        });
    }

    private String stripHtml(String html) {
        final StringBuffer buffer = new StringBuffer();
        SAXParser parser = new SAXParser();
        parser.setContentHandler(new DefaultHandler() {
            public void characters(char[] chars, int i, int i1) throws SAXException {
                buffer.append(chars, i, i1);
                buffer.append(" ");
            }
        });

        if(html != null) {
            try {
                parser.parse(new InputSource(new StringReader(html)));
            } catch (SAXException e) {
                Log.error(SOURCE, e, null, null);
            } catch (IOException e) {
                Log.error(SOURCE, e, null, null);
            }
        }

        return buffer.toString();
    }

    @SuppressWarnings("unchecked")
    private String getArticleText(final Content content) {
        final StringBuilder page = new StringBuilder();

        content.doForEachAttribute(AttributeDataType.CONTENT_DATA, new AttributeHandler() {
            public void handleAttribute(Attribute attribute) {
                if(attribute.getValue() != null && attribute.isSearchable()) {
                    // Dont add title twice
                    if (content.getTitle() == null || !content.getTitle().equals(attribute.getValue())) {
                        page.append(stripHtml(attribute.getValue())).append(" ");
                    }
                }
            }
        });

        return page.toString();
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

    private String getTopics(int contentId) throws SQLException {
        StringBuilder sb = new StringBuilder();
        TmBaseName[] baseNames = aksessDao.getTmBaseNames(contentId);
        for (int i = 0; i < baseNames.length; i++) {
            TmBaseName baseName = baseNames[i];
            sb.append(baseName.getBaseName());
            if(i < baseNames.length -1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private String getAssociations(Content content) {
        StringBuilder sb = new StringBuilder();
        List <Association> associations = content.getAssociations();
        for (int i = 0; i < associations.size(); i++) {
            sb.append(associations.get(i).getAssociationId());
            if (i < associations.size() - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private float calculateBoost(Content content) {
        float boost = 1;
        for (ContentBooster booster : boosters) {
            boost += booster.getBoost(content);
        }
        return boost;
    }

    public void setAksessDao(AksessDao aksessDao) {
        this.aksessDao = aksessDao;
    }

    public void setBoosters(List<ContentBooster> boosters) {
        this.boosters = boosters;
    }

    public void setTextExtractorSelector(TextExtractorSelector textExtractorSelector) {
        this.textExtractorSelector = textExtractorSelector;
    }
}

