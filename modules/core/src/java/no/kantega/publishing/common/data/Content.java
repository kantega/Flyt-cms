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

package no.kantega.publishing.common.data;

import no.kantega.publishing.common.data.enums.*;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.commons.util.HttpHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 *
 */
public class Content extends BaseObject {
    // Information not versionable
    private ContentType type = ContentType.PAGE;
    private int contentTemplateId = 0;  // Id til mal for innhold (må settes)
    private int metaDataTemplateId = 0; // Id til mal metadata
    private int displayTemplateId = 0;  // Id til visningsmal (må settes)
    private int documentTypeId = 0;
    private int documentTypeIdForChildren = 0; //Dokumenttype for undersider til denne.

    private int groupId = 0;

    private String owner = "";
    private String ownerPerson = "";

    private String location = null;
    private boolean openInNewWindow = false;

    private String alias = null; // Brukervennlig URL, settes til "/" for startsida
    private Date createDate = new Date();
    private Date publishDate = null;
    private Date expireDate  = null;
    private Date revisionDate = null;
    private int expireAction = ExpireAction.HIDE;
    private int visibilityStatus = ContentVisibilityStatus.ACTIVE;
    private int numberOfNotes = 0;

    // Versionable information
    private int versionId = -1;
    private int version = 1;
    private int status = -1;
    private int language = 0;

    private String title = "Uten tittel";
    private String altTitle = "";
    private String description = "";
    private String image = "";

    private String keywords = "";
    private String publisher = "";

    private Date lastModified = null;
    private String modifiedBy = null;
    private String approvedBy = null;
    private String changeDescription = null;

    private long forumId = -1;

    // Associations to this content object
    private List associations = new ArrayList();

    // Attributes
    private List contentAttributes = new ArrayList();
    private List metaAttributes = new ArrayList();

    // File attachments
    private List attachments = new ArrayList();

    private List topics = new ArrayList();

    // Status
    boolean isModified = false;
    boolean isCheckedOut = false;
    boolean isLocked = false;


    public Content() {
        lastModified = new Date();
    }

    public int getId() {
        return id;
    }

    public int getObjectType() {
        return ObjectType.CONTENT;
    }

    public int getSecurityId() {
        Association a = getAssociation();
        if (a != null) {
            return a.getSecurityId();
        }
        return -1;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ContentIdentifier getContentIdentifier() {
        ContentIdentifier cid = new ContentIdentifier();
        Association a = getAssociation();
        if (a != null) {
            cid.setAssociationId(a.getAssociationId());
            cid.setSiteId(a.getSiteId());
        }
        cid.setContentId(id);

        return cid;
    }

    public int getVisibilityStatus() {
        return visibilityStatus;
    }

    public void setVisibilityStatus(int visibilityStatus) {
        this.visibilityStatus = visibilityStatus;
    }

    public int getVersionId() {
        return versionId;
    }

    public void setVersionId(int versionId) {
        this.versionId = versionId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getLanguage() {
        return language;
    }


    public void setLanguage(int language) {
        this.language = language;
    }

    public ContentType getType() {
        return type;
    }

    public void setType(ContentType type) {
        this.type = type;
    }

    public int getContentTemplateId() {
        return contentTemplateId;
    }

    public void setContentTemplateId(int templateId) {
        this.contentTemplateId = templateId;
    }

    public int getMetaDataTemplateId() {
        return metaDataTemplateId;
    }

    public void setMetaDataTemplateId(int templateId) {
        this.metaDataTemplateId = templateId;
    }

    public int getDisplayTemplateId() {
        return displayTemplateId;
    }

    public void setDisplayTemplateId(int displayTemplateId) {
        this.displayTemplateId = displayTemplateId;
    }

    public int getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(int documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public int getDocumentTypeIdForChildren() {
        return documentTypeIdForChildren;
    }

    public void setDocumentTypeIdForChildren(int documentTypeIdForChildren) {
        this.documentTypeIdForChildren = documentTypeIdForChildren;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }


    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        if (owner != null) {
            this.owner = owner;
        }
    }

    public Association getAssociation() {
        if (associations.size() == 1) {
            return (Association)associations.get(0);
        }

        for (int i = 0; i < associations.size(); i++) {
            Association tmp = (Association)associations.get(i);
            if (tmp.isCurrent()) {
                return tmp;
            }
        }
        return null;
    }

    public List getAssociations() {
        return associations;
    }

    public void setAssociations(List associations) {
        this.associations = associations;
    }

    public void addAssociation(Association a) {
        associations.add(a);
    }

    public String getLocation() {
        return location;
    }

    public String getUrl() {
        Association a = getAssociation();
        return Aksess.getContextPath() + "/content.ap?thisId=" + a.getAssociationId() + "&amp;language=" + language;
    }

    public String getUrl(HttpServletRequest request) {
        Association a = getAssociation();

        if (alias != null && alias.startsWith("/") && a.getAssociationtype() == AssociationType.DEFAULT_POSTING_FOR_SITE) {
            // Alias brukes når angitt og man har en hovedknytning og man ikke er i adminmodus
            if (HttpHelper.isAdminMode(request)) {
                return Aksess.getContextPath() + alias + "?thisId=" + a.getAssociationId() + "&amp;language=" + language;
            } else {
                return Aksess.getContextPath() + alias;
            }
        }
        return Aksess.getContextPath() + "/content.ap?thisId=" + a.getAssociationId() + "&amp;language=" + language;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        if (alias != null) {
            if (alias.length() == 0) {
                alias = null;
            }
        }
        this.alias = alias;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title != null) {
            if (title.length() > 255) {
                title = title.substring(0, 251) + "...";
            }
            this.title = title;
        }
    }

    public String getAltTitle() {
        return altTitle;
    }

    public void setAltTitle(String altTitle) {
        if (altTitle != null) {
            if (altTitle.length() > 255) {
                altTitle = altTitle.substring(0, 254);
            }
            this.altTitle = altTitle;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description != null) {
            description = description.trim();
            if (description.length() > 500) {
                description = description.replaceAll("<@WEB@>", "@WEB@"); // Replace <@WEB> temporary before stripping tags
                description = description.replaceAll("<(.|\\n)+?>", ""); // Strip tags before cuting
                description = description.replaceAll("@WEB@", "<@WEB@>");
                if (description.length() > 500) {
                    description = description.substring(0, 500) + "...";
                }
            }
            this.description = description;
        }
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        if (keywords != null) {
            this.keywords = keywords;
        }
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        if (publisher != null) {
            if (publisher.length() > 64) {
                publisher = publisher.substring(0, 63);
            }
            this.publisher = publisher;
        }
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        if (modifiedBy != null && modifiedBy.length() > 64) {
            modifiedBy = modifiedBy.substring(0, 63);
        }
        this.modifiedBy = modifiedBy;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        if (approvedBy != null && approvedBy.length() > 64) {
            approvedBy = approvedBy.substring(0, 63);
        }
        this.approvedBy = approvedBy;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public int getExpireAction() {
        return expireAction;
    }

    public void setExpireAction(int expireAction) {
        this.expireAction = expireAction;
    }

    public List getAttributes(int type) {
        if (type == AttributeDataType.CONTENT_DATA) {
            return contentAttributes;
        } else {
            return metaAttributes;
        }
    }

    public void setAttributes(List attr, int type) {
        if (type == AttributeDataType.CONTENT_DATA) {
            contentAttributes = attr;
        } else {
            metaAttributes = attr;
        }
    }

    public String getAttributeValue(String name) {
        Attribute attr = getAttribute(name, AttributeDataType.CONTENT_DATA);
        if (attr != null) {
            String value = attr.getValue();
            if (value == null) {
                return "";
            } else {
                return attr.getValue();
            }
        } else {
            return "";
        }
    }

    public String getMetaAttributeValue(String name) {
        Attribute attr = getAttribute(name, AttributeDataType.META_DATA);
        if (attr != null) {
            return attr.getValue();
        } else {
            return "";
        }
    }

    public Attribute getAttribute(String name, int type) {
        if (name == null || name.length() == 0) {
            return null;
        }
        List list = null;
        if (type == AttributeDataType.CONTENT_DATA) {
            list = contentAttributes;
        } else if (type == AttributeDataType.META_DATA) {
            list = metaAttributes;
        } else {
            list = new ArrayList();
            list.addAll(contentAttributes);
            list.addAll(metaAttributes);
        }

        for (int i = 0; i < list.size(); i++) {
            Attribute attr = (Attribute)list.get(i);
            if (attr.getName().equalsIgnoreCase(name)) {
                return attr;
            }
        }
        return null;
    }

    public void addAttribute(Attribute attr, int type) {
        if (type == AttributeDataType.CONTENT_DATA) {
            contentAttributes.add(attr);
        } else {
            metaAttributes.add(attr);
        }
    }

    public void removeAttribute(String name, int type) {
        List list = null;
        if (type == AttributeDataType.CONTENT_DATA) {
            list = contentAttributes;
        } else {
            list = metaAttributes;
        }

        for (int i = 0; i < list.size(); i++) {
            Attribute attr = (Attribute)list.get(i);
            if (attr.getName().equalsIgnoreCase(name)) {
                list.remove(attr);
            }
        }
    }

    public List getAttachments() {
        return attachments;
    }

    public void addAttachment(Attachment attachment) {
        attachments.add(attachment);
    }

    public List getTopics() {
        return topics;
    }


    public void setTopics(List topics) {
        this.topics = topics;
    }


    public void addTopic(Topic topic) {
        if (topics == null) {
            topics = new ArrayList();
        }
        boolean found = false;
        for (int i = 0; i < topics.size(); i++) {
            Topic t = (Topic)topics.get(i);
            if (t.getId().equalsIgnoreCase(topic.getId()) && t.getTopicMapId() == topic.getTopicMapId()) {
                found = true;
                break;
            }
        }
        if (!found) {
            topics.add(topic);
        }
    }


    public boolean isModified() {
        return isModified;
    }

    public void setIsModified(boolean isModified) {
        this.isModified = isModified;
    }

    public boolean isCheckedOut() {
        return isCheckedOut;
    }

    public void setIsCheckedOut(boolean isCheckedOut) {
        this.isCheckedOut = isCheckedOut;
    }

    public boolean isExternalLink() {
        if (type == ContentType.LINK) {
            if (location != null && location.length() > 0 && location.charAt(0) != '/') {
                return true;
            }
        }
        return false;
    }

    public boolean isOpenInNewWindow() {
        return openInNewWindow;
    }

    public void setDoOpenInNewWindow(boolean openInNewWindow) {
        this.openInNewWindow = openInNewWindow;
    }

    public void setNumberOfNotes(int numberOfNotes) {
        this.numberOfNotes = numberOfNotes;
    }

    public int getNumberOfNotes() {
        return numberOfNotes;
    }

    public String getOwnerPerson() {
        return ownerPerson;
    }

    public void setOwnerPerson(String ownerPerson) {
        this.ownerPerson = ownerPerson;
    }

    public String getChangeDescription() {
        return changeDescription;
    }

    public void setChangeDescription(String changeDescription) {
        this.changeDescription = changeDescription;
    }

    public Date getRevisionDate() {
        return revisionDate;
    }

    public void setRevisionDate(Date revisionDate) {
        this.revisionDate = revisionDate;
    }

    public long getForumId() {
        return forumId;
    }

    public void setForumId(long forumId) {
        this.forumId = forumId;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }
}