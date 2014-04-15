package no.kantega.openaksess.search.provider.transformer;

import no.kantega.openaksess.search.index.ContentAttributeNameToIndexFieldMapping;
import no.kantega.publishing.api.content.Language;
import no.kantega.publishing.common.cache.DocumentTypeCache;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.DocumentType;
import no.kantega.publishing.common.data.attributes.*;
import no.kantega.publishing.topicmaps.ao.TopicDao;
import no.kantega.search.api.IndexableDocument;
import no.kantega.search.api.IndexableDocumentCustomizer;
import no.kantega.search.api.provider.DocumentTransformerAdapter;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static no.kantega.openaksess.search.provider.transformer.LocationUtil.locationWithoutTrailingSlash;
import static no.kantega.publishing.api.content.Language.getLanguageAsISOCode;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class ContentTransformer extends DocumentTransformerAdapter<Content> {

    public ContentTransformer() {
        super(Content.class);
    }

    private final Logger log = LoggerFactory.getLogger(getClass());
    public static final String HANDLED_DOCUMENT_TYPE = "aksess-document";

    private static final Pattern KEYWORDS_PATTERN = Pattern.compile("[,\\s]");
    private static final Pattern TITLE_OR_DESCRIPTION = Pattern.compile("title|description");

    @Autowired(required = false)
    private ContentAttributeNameToIndexFieldMapping contentAttributeNameToIndexFieldMapping;

    @Autowired
    private TopicDao topicDao;

    @Override
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
            indexableDocument.setVisibility(content.getVisibilityStatus().name());
            int language = content.getLanguage();
            indexableDocument.setLanguage(getLanguageAsISOCode(language));
            int siteId = association.getSiteId();
            indexableDocument.setSiteId(siteId);
            indexableDocument.setParentId(association.getParentAssociationId());

            indexableDocument.addAttribute("location", locationWithoutTrailingSlash(association));
            indexableDocument.addAttribute("location_depth", association.getDepth());

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

            for(Attribute value : getAttributes(content)){
                String customFieldNameMapping = getCustomIndexFieldMapping(content, value);
                String fieldName = getFieldName(value, language);

                if (shouldIndexAttribute(value, fieldName)) {
                    indexAttribute(indexableDocument, value, fieldName, customFieldNameMapping);
                } else {
                    log.debug("Did not index attribute {} with value «{}»", fieldName, value.getValue());
                }
            }

            for (IndexableDocumentCustomizer<Content> customizer : getIndexableDocumentCustomizers()) {
                indexableDocument = customizer.customizeIndexableDocument(content, indexableDocument);
            }
        }

        return indexableDocument;
    }

    private List<Attribute> getAttributes(Content content) {
        Collection<Attribute> values = content.getContentAttributes().values();
        List<Attribute> attributes = new ArrayList<>(values.size());
        for (Attribute value : values) {
            if(value instanceof RepeaterAttribute){
                Iterator<List<Attribute>> iterator = ((RepeaterAttribute) value).getIterator();
                while (iterator.hasNext()){
                    attributes.addAll(iterator.next());
                }
            } else {
                attributes.add(value);
            }
        }
        return attributes;
    }

    private void indexAttribute(IndexableDocument indexableDocument, Attribute value, String calculatedFieldName,  String customFieldNameMapping) {
        if(isNotBlank(customFieldNameMapping)){
            log.debug("Attribute {} is indexed as {}", value.getName(), customFieldNameMapping);
            indexableDocument.addAttribute(customFieldNameMapping, getValue(value));
        } else if(isNotTitleOrDescription(value)){

            indexableDocument.addAttribute(calculatedFieldName, getValue(value));
        }
    }

    private boolean shouldIndexAttribute(Attribute value, String fieldName) {
        return value.isSearchable() && isNotBlankIntegerField(value, fieldName);
    }

    private boolean isNotBlankIntegerField(Attribute value, String fieldName) {
        boolean isBlankIntegerField = fieldName.endsWith("_i") && isBlank(value.getValue());
        return !isBlankIntegerField;
    }

    private boolean isNotTitleOrDescription(Attribute value) {
        return !TITLE_OR_DESCRIPTION.matcher(value.getName()).matches();
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
            if (documentTypeById != null) {
                indexableDocument.addAttribute("documentTypeName", documentTypeById.getName());
            }
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
        StringBuilder fieldname = new StringBuilder();
        if(attribute.getParent() != null){
            fieldname.append(attribute.getParent().getName()).append("_");
        }
        fieldname.append(attribute.getName());
        fieldname.append("_");
        if((attribute instanceof ListAttribute && !(attribute instanceof ContentlistAttribute)) ||
                attribute instanceof EmailAttribute ||
                attribute instanceof RoleAttribute ||
                attribute instanceof UserAttribute ||
                attribute instanceof TopicmapAttribute ||
                attribute instanceof TopicAttribute){
            fieldname.append("txt");
        } else if(attribute instanceof ContentidAttribute ||
                attribute instanceof ContentlistAttribute ||
                attribute instanceof FileAttribute ||
                attribute instanceof NumberAttribute){
            fieldname.append("i");
        } else if(attribute instanceof DateAttribute){
            fieldname.append("dt");
        } else if (attribute instanceof TextAttribute){
            if (isEnglishContent(attribute, language)) {
                fieldname.append("en");
            }else {
                fieldname.append("no");
            }
        } else if (attribute instanceof UrlAttribute){
            fieldname.append("str");
        }
        return fieldname.toString();
    }

    private boolean isEnglishContent(Attribute attribute, int language) {
        return language == Language.ENGLISH || attribute.getName().endsWith("_en") || attribute.getName().endsWith("_eng");
    }

    private Object getValue(Attribute attribute) {
        Object value;
        if(attribute instanceof DateAttribute){
            value = ((DateAttribute) attribute).getValueAsDate();
        }else if(attribute instanceof HtmltextAttribute){
            value = stripHtml(attribute.getValue());
        } else if(attribute instanceof ListAttribute ){
            value = ((ListAttribute) attribute).getValues();
        } else if (attribute instanceof ContentidAttribute) {
            value = asList(StringUtils.split(attribute.getValue(), ','));
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

    @Override
    public String generateUniqueID(Content document) {
        return HANDLED_DOCUMENT_TYPE + "-" + document.getId() + "-" + document.getAssociation().getSiteId();
    }

    public String generateUniqueID(Content document, int siteId) {
        return HANDLED_DOCUMENT_TYPE + "-" + document.getId() + "-" + siteId;
    }

    private String getCustomIndexFieldMapping(Content content, Attribute value) {
        if(contentAttributeNameToIndexFieldMapping != null) {
            return contentAttributeNameToIndexFieldMapping.getMappings().get(content.getContentTemplateId() + "," + value.getName());
        } else {
            return null;
        }
    }
}
