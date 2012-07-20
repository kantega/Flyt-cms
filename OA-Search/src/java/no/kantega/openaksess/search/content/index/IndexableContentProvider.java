package no.kantega.openaksess.search.content.index;

import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.DateAttribute;
import no.kantega.publishing.common.data.enums.ContentStatus;
import no.kantega.publishing.common.data.enums.ContentVisibilityStatus;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.search.api.IndexableDocument;
import no.kantega.search.api.provider.IndexableDocumentProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

import static no.kantega.publishing.common.data.enums.Language.getLanguageAsISOCode;

@Component
public class IndexableContentProvider implements IndexableDocumentProvider {
    private static final String HANDLED_DOCUMENT_TYPE = "aksess-document";

    @Autowired
    private ContentManagementService contentManagementService;

    @Autowired
    @Qualifier("aksessDataSource")
    private DataSource dataSource;

    public Iterator<IndexableDocument> provideDocuments() {

        return new IndexableContentDocumentIterator(dataSource, contentManagementService);
    }

    private IndexableDocument getIndexableDocument(Content content){
        IndexableDocument indexableDocument = new IndexableDocument();
        indexableDocument.setId(String.valueOf(content.getId()));
        indexableDocument.setTitle(content.getTitle());
        indexableDocument.setContentType(IndexableContentProvider.HANDLED_DOCUMENT_TYPE);
        indexableDocument.setDescription(content.getDescription());
        indexableDocument.setContentStatus(ContentStatus.getContentStatusAsString(content.getStatus()));
        indexableDocument.setVisibility(ContentVisibilityStatus.getName(content.getVisibilityStatus()));
        indexableDocument.setSiteId(content.getAssociation().getSiteId());
        indexableDocument.setLanguage(getLanguageAsISOCode(content.getLanguage()));

        Association association = content.getAssociation();
        indexableDocument.addAttribute("location", association.getPath());

        indexableDocument.addAttribute("contentType", content.getType().name());
        indexableDocument.addAttribute("contentTemplateId", content.getContentTemplateId());
        indexableDocument.addAttribute("metaDataTemplateId", content.getMetaDataTemplateId());
        indexableDocument.addAttribute("displayTemplateId", content.getDisplayTemplateId());
        indexableDocument.addAttribute("documentTypeId", content.getDocumentTypeId());
        indexableDocument.addAttribute("groupId", content.getGroupId());
        indexableDocument.addAttribute("alias", content.getAlias());
        indexableDocument.addAttribute("createDate", content.getCreateDate());
        indexableDocument.addAttribute("publishDate", content.getPublishDate());
        indexableDocument.addAttribute("expireDate", content.getExpireDate());
        indexableDocument.addAttribute("author", content.getOwnerPerson());
        indexableDocument.addAttribute("keywords", content.getKeywords());
        indexableDocument.addAttribute("url", content.getUrl());

        for(Map.Entry<String, Attribute> attribute : content.getContentAttributes().entrySet()){
            Attribute value = attribute.getValue();
            if(value.isSearchable()){
                String fieldName = value.getName();

                indexableDocument.addAttribute(fieldName, getValue(value));
            }
        }
        return indexableDocument;
    }

    private Object getValue(Attribute attribute) {
        Object value;
        if(attribute instanceof DateAttribute){
            value = ((DateAttribute) attribute).getValueAsDate();
        }else {
            value = attribute.getValue();
        }
        return value;
    }

    private class IndexableContentDocumentIterator implements Iterator<IndexableDocument>{


        private final ResultSet resultSet;
        private final ContentManagementService cms;

        private IndexableContentDocumentIterator(DataSource dataSource, ContentManagementService cms) {
            this.cms = cms;
            try {
                Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT ContentId FROM content");
                resultSet = preparedStatement.executeQuery();
            } catch (SQLException e) {
                throw new IllegalStateException("Could not connect to database", e);
            }
        }

        public boolean hasNext() {
            try {
                return resultSet.next();
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        }

        public IndexableDocument next() {
            try {
                ContentIdentifier contentIdentifier = new ContentIdentifier();
                contentIdentifier.setContentId(resultSet.getInt("ContentId"));
                return getIndexableDocument(cms.getContent(contentIdentifier));
            } catch (Exception e) {
              throw new IllegalStateException(e);
            }
        }

        public void remove() {
            throw new UnsupportedOperationException("Remove is not supported");
        }
    }
}
