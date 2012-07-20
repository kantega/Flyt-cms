package no.kantega.search.solr;

import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.common.ao.ContentHandler;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.*;
import no.kantega.publishing.common.data.enums.ContentStatus;
import no.kantega.publishing.common.data.enums.ContentVisibilityStatus;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;

import static no.kantega.publishing.common.data.enums.Language.getLanguageAsISOCode;

@Component
public class ContentIndexer {
    @Autowired
    private SolrServer solrServer;

    @PostConstruct
    public void reindex() throws IOException, SolrServerException {
        solrServer.deleteByQuery( "*:*" );
        ContentHandler contentHandler = new ContentHandler() {
            public void handleContent(Content content) {
                SolrInputDocument document = new SolrInputDocument();
                Association association = content.getAssociation();
                String contenttype = "Content";
                document.addField("contenttype", contenttype);
                int associationId = association.getId();
                document.addField("id", associationId);
                document.addField("uid", contenttype.concat(String.valueOf(associationId)));
                document.addField("title_no", content.getTitle(), 1.0f);
                document.addField("altTitle_no", content.getAltTitle(), 1.0f);
                document.addField("description_no", content.getDescription(), 1.0f);
                document.addField("contentType", content.getType().name());
                document.addField("contentTemplateId", content.getContentTemplateId());
                document.addField("contentTemplateName", getContentTemplateName(content));
                document.addField("metaDataTemplateId", content.getMetaDataTemplateId());
                document.addField("displayTemplateId", content.getDisplayTemplateId());
                document.addField("displayTemplateName", getDisplayTemplateName(content));
                document.addField("documentTypeId", content.getDocumentTypeId());
                document.addField("groupId", content.getGroupId());
                document.addField("siteId", content.getGroupId());
                document.addField("location", association.getPath());
                document.addField("alias", content.getAlias(), 1.0f);
                document.addField("createDate", content.getCreateDate());
                document.addField("publishDate", content.getPublishDate());
                document.addField("expireDate", content.getExpireDate());
                document.addField("author", content.getOwnerPerson());
                document.addField("keywords", content.getKeywords());
                document.addField("language", getLanguageAsISOCode(content.getLanguage()));
                document.addField("visibilityStatus", getVisibilityStatusAsString(content.getVisibilityStatus()));
                document.addField("contentStatus", getContentStatusAsString(content.getVisibilityStatus()));
                document.addField("url", content.getUrl());

                for(Map.Entry<String, Attribute> attribute : content.getContentAttributes().entrySet()){
                    Attribute value = attribute.getValue();
                    if(value.isSearchable()){
                        String fieldName = getFieldName(value);

                        document.addField(fieldName, getValue(value));
                    }
                }

                try {
                    solrServer.add(document);
                } catch (SolrServerException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            private String getContentStatusAsString(int contentStatus) {
                switch (contentStatus){
                    case ContentStatus.ARCHIVED:
                        return "ARCHIVED";
                    case ContentStatus.DRAFT:
                        return "DRAFT";
                    case ContentStatus.HEARING:
                        return "HEARING";
                    case ContentStatus.PUBLISHED:
                        return "PUBLISHED";
                    case ContentStatus.PUBLISHED_WAITING:
                        return "PUBLISHED_WAITING";
                    case ContentStatus.REJECTED:
                        return "REJECTED";
                    case ContentStatus.WAITING_FOR_APPROVAL:
                        return "WAITING_FOR_APPROVAL";
                    default:
                        return "UNKNOWN";
                }
            }

            private String getVisibilityStatusAsString(int visibilityStatus) {
                return ContentVisibilityStatus.getName(visibilityStatus);
            }

            private String getDisplayTemplateName(Content content) {
                return "Herp derp!";
            }

            private String getContentTemplateName(Content content) {
                return "Derp herp!";
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
        };
        ContentAO.ContentHandlerStopper stopper = new ContentAO.ContentHandlerStopper() {
            public boolean isStopRequested() {
                return false;
            }
        };

        ContentAO.forAllContentObjects(contentHandler, stopper, 10);

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
}
