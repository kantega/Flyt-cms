package no.kantega.openaksess.search.provider.transformer;

import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.*;
import no.kantega.publishing.common.data.enums.ContentStatus;
import no.kantega.publishing.common.data.enums.ContentVisibilityStatus;
import no.kantega.search.api.IndexableDocument;
import no.kantega.search.api.provider.DocumentTransformer;
import org.springframework.stereotype.Component;

import java.util.Map;

import static no.kantega.publishing.common.data.enums.Language.getLanguageAsISOCode;

@Component
public class ContentTransformer implements DocumentTransformer<Content> {
    private static final String HANDLED_DOCUMENT_TYPE = "aksess-document";

    public IndexableDocument transform(Content content) {
        IndexableDocument indexableDocument = new IndexableDocument(generateUniqueID(content));

        if (content.isSearchable()) {
            indexableDocument.setShouldIndex(true);
            indexableDocument.setId(String.valueOf(content.getId()));
            indexableDocument.setTitle(content.getTitle());
            indexableDocument.setContentType(HANDLED_DOCUMENT_TYPE);
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
            // TODO get author name
            indexableDocument.addAttribute("author", content.getOwnerPerson());
            indexableDocument.addAttribute("keywords", content.getKeywords());
            indexableDocument.addAttribute("url", content.getUrl());

            for(Map.Entry<String, Attribute> attribute : content.getContentAttributes().entrySet()){
                Attribute value = attribute.getValue();
                if(value.isSearchable()){
                    String fieldName = getFieldName(value);

                    indexableDocument.addAttribute(fieldName, getValue(value));
                }
            }
        }
        return indexableDocument;
    }

    private String getFieldName(Attribute attribute) {
        StringBuilder fieldname = new StringBuilder(attribute.getName());
        fieldname.append("_");
        if(attribute instanceof CategoryAttribute ||
                attribute instanceof ListAttribute ||
                attribute instanceof EmailAttribute ||
                attribute instanceof RoleAttribute ||
                attribute instanceof UserAttribute ||
                attribute instanceof TopicmapAttribute ||
                attribute instanceof TopicAttribute ||
                attribute instanceof UrlAttribute){
            fieldname.append("txt");
        }else if(attribute instanceof ContentidAttribute ||
                attribute instanceof FileAttribute ||
                attribute instanceof NumberAttribute){
            fieldname.append("i");
        }else if(attribute instanceof DateAttribute){
            fieldname.append("dt");
        }else if (attribute instanceof RepeaterAttribute){
            fieldname = new StringBuilder(getFieldName(attribute.getParent()));
        } else if (attribute instanceof TextAttribute){
            fieldname.append("no");
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
}
