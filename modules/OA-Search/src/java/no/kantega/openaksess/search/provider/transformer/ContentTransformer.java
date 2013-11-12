package no.kantega.openaksess.search.provider.transformer;

import no.kantega.publishing.api.content.Language;
import no.kantega.publishing.common.cache.DocumentTypeCache;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.DocumentType;
import no.kantega.publishing.common.data.attributes.*;
import no.kantega.publishing.common.data.enums.ContentVisibilityStatus;
import no.kantega.publishing.topicmaps.ao.TopicDao;
import no.kantega.search.api.IndexableDocument;
import no.kantega.search.api.provider.DocumentTransformer;
import org.cyberneko.html.parsers.SAXParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static no.kantega.openaksess.search.provider.transformer.LocationUtil.locationWithoutTrailingSlash;
import static no.kantega.publishing.api.content.Language.getLanguageAsISOCode;
import static org.apache.commons.lang.StringUtils.isNotBlank;

@Component
public class ContentTransformer implements DocumentTransformer<Content> {
    private final Logger log = LoggerFactory.getLogger(getClass());
    public static final String HANDLED_DOCUMENT_TYPE = "aksess-document";

    private static final Pattern KEYWORDS_PATTERN = Pattern.compile("[,\\s]");
    private static final Pattern TITLE_OR_DESCRIPTION = Pattern.compile("title|description");

    @Autowired
    private TopicDao topicDao;

    public IndexableDocument transform(Content content) {
        IndexableDocument indexableDocument = new IndexableDocument(generateUniqueID(content));

        if (content.isSearchable()) {
            indexableDocument.setShouldIndex(true);
            Association association = content.getAssociation();

            indexableDocument.setId(String.valueOf(content.getId()));
            indexableDocument.addAttribute("associationId", association.getAssociationId());
            indexableDocument.setSecurityId(content.getSecurityId());
            indexableDocument.setTitle(content.getTitle());
            indexableDocument.setContentType(HANDLED_DOCUMENT_TYPE);
            indexableDocument.setDescription(content.getDescription());
            indexableDocument.setContentStatus(content.getStatus().name());
            indexableDocument.setVisibility(ContentVisibilityStatus.getName(content.getVisibilityStatus()));
            int language = content.getLanguage();
            indexableDocument.setLanguage(getLanguageAsISOCode(language));

            int siteId = association.getSiteId();
            indexableDocument.setSiteId(siteId);
            indexableDocument.setParentId(association.getParentAssociationId());
            indexableDocument.addAttribute("location", locationWithoutTrailingSlash(association));

            indexableDocument.addAttribute("contentType", content.getType().name());
            indexableDocument.addAttribute("contentTemplateId", content.getContentTemplateId());

            setMetaTemplateId(content, indexableDocument);

            indexableDocument.addAttribute("displayTemplateId", content.getDisplayTemplateId());
            setDocumentTypeIdAndName(content, indexableDocument);

            indexableDocument.addAttribute("groupId", content.getGroupId());
            setAlias(content, indexableDocument);
            indexableDocument.addAttribute("createDate", content.getCreateDate());
            indexableDocument.addAttribute("publishDate", content.getPublishDate());
            indexableDocument.addAttribute("lastModified", content.getLastModified());
            indexableDocument.addAttribute("expireDate", content.getExpireDate());

            setOwnerPerson(content, indexableDocument);

            indexableDocument.addAttribute("keywords", getKeywords(content));
            indexableDocument.addAttribute("url", content.getUrl());
            indexableDocument.addAttribute("topics", topicDao.getTopicNamesForContent(content.getId()));

            for(Map.Entry<String, Attribute> attribute : content.getContentAttributes().entrySet()){
                Attribute value = attribute.getValue();
                if(value.isSearchable() && !TITLE_OR_DESCRIPTION.matcher(value.getName()).matches()){
                    String fieldName = getFieldName(value, language);

                    indexableDocument.addAttribute(fieldName, getValue(value));
                }
            }
        }
        return indexableDocument;
    }

    private void setOwnerPerson(Content content, IndexableDocument indexableDocument) {
        String ownerPerson = content.getOwnerPerson();
        if (isNotBlank(ownerPerson)) {
            indexableDocument.addAttribute("author", ownerPerson);
        }
    }

    private void setAlias(Content content, IndexableDocument indexableDocument) {
        String alias = content.getAlias();
        if (isNotBlank(alias)) {
            indexableDocument.addAttribute("alias", alias);
        }
    }

    private void setDocumentTypeIdAndName(Content content, IndexableDocument indexableDocument) {
        int documentTypeId = content.getDocumentTypeId();

        if (documentTypeId != -1 && documentTypeId != 0) {
            indexableDocument.addAttribute("documentTypeId", documentTypeId);
            DocumentType documentTypeById = DocumentTypeCache.getDocumentTypeById(documentTypeId);
            indexableDocument.addAttribute("documentTypeName", documentTypeById.getName());
        }
    }

    private void setMetaTemplateId(Content content, IndexableDocument indexableDocument) {
        int metaDataTemplateId = content.getMetaDataTemplateId();
        if (metaDataTemplateId != -1 && metaDataTemplateId != 0) {
            indexableDocument.addAttribute("metaDataTemplateId", metaDataTemplateId);
        }
    }

    private List<String> getKeywords(Content content) {
        List<String> filteredKeywords = new ArrayList<>();
        String contentKeywords = content.getKeywords();
        if (contentKeywords != null) {
            String[] keywords = KEYWORDS_PATTERN.split(contentKeywords);
            for (String keyword : keywords) {
                if(isNotBlank(keyword) && !keyword.equalsIgnoreCase("null")){
                    filteredKeywords.add(keyword);
                }
            }
        }
        return filteredKeywords;
    }

    private String getFieldName(Attribute attribute, int language) {
        StringBuilder fieldname = new StringBuilder(attribute.getName());
        fieldname.append("_");
        if(attribute instanceof ListAttribute ||
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
            fieldname.append("str");
        }
        return fieldname.toString();
    }

    private Object getValue(Attribute attribute) {
        Object value;
        if(attribute instanceof DateAttribute){
            value = ((DateAttribute) attribute).getValueAsDate();
        }else if(attribute instanceof HtmltextAttribute){
            value = stripHtml(attribute.getValue());
        } else {
            value = attribute.getValue();
        }
        return value;
    }

    private String stripHtml(String html) {
        final StringBuilder buffer = new StringBuilder();
        SAXParser parser = new SAXParser();
        parser.setContentHandler(new DefaultHandler() {
            public void characters(char[] chars, int i, int i1) {
                buffer.append(chars, i, i1);
                buffer.append(" ");
            }
        });

        if(html != null) {
            try (StringReader stringReader = new StringReader(html)){
                parser.parse(new InputSource(stringReader));
            } catch (IOException | SAXException e) {
                log.error("Error stripping html", e);
            }
        }

        return buffer.toString();
    }

    public String generateUniqueID(Content document) {
        return String.format("%s-%s", HANDLED_DOCUMENT_TYPE, document.getId());
    }
}
