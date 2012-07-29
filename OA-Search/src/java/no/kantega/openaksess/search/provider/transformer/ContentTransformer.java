package no.kantega.openaksess.search.provider.transformer;

import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.*;
import no.kantega.publishing.common.data.enums.ContentStatus;
import no.kantega.publishing.common.data.enums.ContentVisibilityStatus;
import no.kantega.publishing.common.data.enums.Language;
import no.kantega.search.api.IndexableDocument;
import no.kantega.search.api.provider.DocumentTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static no.kantega.publishing.common.data.enums.Language.getLanguageAsISOCode;
import static org.apache.commons.lang.StringUtils.isNotBlank;

@Component
public class ContentTransformer implements DocumentTransformer<Content> {
    public static final String HANDLED_DOCUMENT_TYPE = "aksess-document";

    private JdbcTemplate jdbcTemplate;

    public IndexableDocument transform(Content content) {
        IndexableDocument indexableDocument = new IndexableDocument(generateUniqueID(content));

        if (content.isSearchable()) {
            indexableDocument.setShouldIndex(true);
            indexableDocument.setId(String.valueOf(content.getId()));
            indexableDocument.setSecurityId(content.getSecurityId());
            indexableDocument.setTitle(content.getTitle());
            indexableDocument.setContentType(HANDLED_DOCUMENT_TYPE);
            indexableDocument.setDescription(content.getDescription());
            indexableDocument.setContentStatus(ContentStatus.getContentStatusAsString(content.getStatus()));
            indexableDocument.setVisibility(ContentVisibilityStatus.getName(content.getVisibilityStatus()));
            indexableDocument.setSiteId(content.getAssociation().getSiteId());
            int language = content.getLanguage();
            indexableDocument.setLanguage(getLanguageAsISOCode(language));

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
            // TODO get author name
            indexableDocument.addAttribute("author", content.getOwnerPerson());
            indexableDocument.addAttribute("keywords", getKeywords(content));
            indexableDocument.addAttribute("url", content.getUrl());
            indexableDocument.addAttribute("topics", getTopics(content.getId()));

            for(Map.Entry<String, Attribute> attribute : content.getContentAttributes().entrySet()){
                Attribute value = attribute.getValue();
                if(value.isSearchable() && !value.getName().matches("title|description")){
                    String fieldName = getFieldName(value, language);

                    indexableDocument.addAttribute(fieldName, getValue(value));
                }
            }
        }
        return indexableDocument;
    }

    private List<String> getKeywords(Content content) {
        List<String> filteredKeywords = new ArrayList<String>();
        String contentKeywords = content.getKeywords();
        if (contentKeywords != null) {
            String[] keywords = contentKeywords.split("[,\\s]");
            for (String keyword : keywords) {
                if(isNotBlank(keyword) && !keyword.equalsIgnoreCase("null")){
                    filteredKeywords.add(keyword);
                }
            }
        }
        return filteredKeywords;
    }

    private List<String> getTopics(int contentId) {
        return jdbcTemplate.queryForList("SELECT tmbasename.Basename " +
                "FROM ct2topic, tmbasename " +
                "WHERE ContentId=? " +
                "AND tmbasename.TopicId=ct2topic.TopicId " +
                "AND tmbasename.TopicMapId=ct2topic.TopicMapId", String.class, contentId);
    }

    private String getFieldName(Attribute attribute, int language) {
        StringBuilder fieldname = new StringBuilder(attribute.getName());
        fieldname.append("_");
        if(attribute instanceof CategoryAttribute ||
                attribute instanceof ListAttribute ||
                attribute instanceof EmailAttribute ||
                attribute instanceof RoleAttribute ||
                attribute instanceof UserAttribute ||
                attribute instanceof TopicmapAttribute ||
                attribute instanceof TopicAttribute){
            fieldname.append("txt");
        }else if(attribute instanceof ContentidAttribute ||
                attribute instanceof FileAttribute ||
                attribute instanceof NumberAttribute){
            fieldname.append("i");
        }else if(attribute instanceof DateAttribute){
            fieldname.append("dt");
        }else if (attribute instanceof RepeaterAttribute){
            fieldname = new StringBuilder(getFieldName(attribute.getParent(), language));
        } else if (attribute instanceof TextAttribute){
            if (language == Language.ENGLISH) {
                fieldname.append("en");
            }else {
                fieldname.append("no");
            }
        } else if (attribute instanceof UrlAttribute){
            fieldname.append("url");
        }
        return fieldname.toString();
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

    public String generateUniqueID(Content document) {
        return String.format("%s-%s", HANDLED_DOCUMENT_TYPE, document.getId());
    }

    @Autowired
    @Qualifier("aksessDataSource")
    public void setDataSource(DataSource dataSource){
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
}
