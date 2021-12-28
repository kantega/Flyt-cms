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

import no.kantega.commons.util.HttpHelper;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.ContentStatus;
import no.kantega.publishing.api.content.attribute.AttributeDataType;
import no.kantega.publishing.api.model.BaseObject;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.AttributeHandler;
import no.kantega.publishing.common.data.attributes.RepeaterAttribute;
import no.kantega.publishing.common.data.enums.*;
import no.kantega.publishing.common.util.PrettyURLEncoder;
import no.kantega.publishing.topicmaps.data.Topic;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static no.kantega.publishing.api.content.attribute.AttributeDataType.CONTENT_DATA;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 *
 */
public class Content extends BaseObject {
    // Information not versionable
    private ContentType type = ContentType.PAGE;
    private int contentTemplateId = 0;
    private int metaDataTemplateId = 0;
    private int displayTemplateId = 0;
    private int documentTypeId = 0;
    private int documentTypeIdForChildren = 0;

    private int groupId = 0;

    /**
     * The creator of the content
     */
    private String creator = "";

    /**
     * The department owning the content, or blank.
     */
    private String owner = "";

    /**
     * Person owning the content
     */
    private String ownerPerson = "";

    private String location = null;
    private boolean openInNewWindow = false;

    /**
     * User friendly url. "/" for the front page.
     */
    private String alias = null;
    private Date createDate = new Date();
    private Date publishDate = null;
    private Date expireDate  = null;
    private Date revisionDate = null;
    private Date changeFromDate = null;
    private ExpireAction expireAction = ExpireAction.HIDE;
    private ContentVisibilityStatus visibilityStatus = ContentVisibilityStatus.ACTIVE;
    private int numberOfNotes = 0;

    // Versionable information
    private int versionId = -1;
    private int version = 1;

    /**
     * The publishing status of this content. One of the values in ContentStatus
     */
    private ContentStatus status = ContentStatus.DRAFT;

    /**
     * On of {code}no.kantega.publishing.api.content.Language{code}
     */
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
    private List<Association> associations = new ArrayList<>();

    // Attributes
    private List<Attribute> contentAttributes = new ArrayList<>();
    private List<Attribute> metaAttributes = new ArrayList<>();

    // File attachments - only used to hold attachments when editing a page which is not saved yet
    private List<Attachment> attachments = new ArrayList<>();

    // Multimedia - only used to hold multimedia when editing a page which is not saved yet
    private List<Multimedia> multimedia = new ArrayList<>();

    // Topics
    private List<Topic> topics = new ArrayList<>();

    private Map<String, Attribute> attributeIndex = new HashMap<>();

    // Status
    boolean isModified = false;
    boolean isCheckedOut = false;
    boolean isLocked = false;

    boolean isSearchable = true;
    boolean isMinorChange = false;

    private Date lastMajorChange;
    private String lastMajorChangeBy;

    // Rating
    private float ratingScore = 0f;
    private int numberOfRatings = 0;

    // Comments
    private int numberOfComments = 0;

    private Hearing hearing = new Hearing();

    private boolean attributesAreUpdatedFromTemplate = false;

    public Content() {
        lastModified = new Date();
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Content content = (Content) o;

        Association a1 = content.getAssociation();
        Association a2 = getAssociation();
        if (a1 != null && a2 != null) {
            if (a2.getId() == -1) {
                return super.equals(o);
            }
            return a1.getId() == a2.getId();
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (getAssociation() != null && getAssociation().getId() != -1) {
            return getAssociation().getId();
        } else {
            return super.hashCode();
        }
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

    public ContentVisibilityStatus getVisibilityStatus() {
        return visibilityStatus;
    }

    public void setVisibilityStatus(ContentVisibilityStatus visibilityStatus) {
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

    public ContentStatus getStatus() {
        return status;
    }

    public void setStatus(ContentStatus status) {
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

    /**
     * @return the databaseId of this Content's ContentTemplate.
     */
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

    /**
     * @return the databaseId of this Content's DisplayTemplate.
     */
    public int getDisplayTemplateId() {
        return displayTemplateId;
    }

    public void setDisplayTemplateId(int displayTemplateId) {
        this.displayTemplateId = displayTemplateId;
    }

    public boolean hasDisplayTemplate() {
        return displayTemplateId > 0;
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

    /**
     * The department owning the content, or blank.
    */
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
            return associations.get(0);
        }

        for (Association association : associations) {
            if (association.isCurrent()) {
                return association;
            }
        }
        return null;
    }

    public List<Association> getAssociations() {
        return associations;
    }

    public void setAssociations(List<Association> associations) {
        this.associations = associations;
    }

    public void addAssociation(Association a) {
        associations.add(a);
    }

    public String getLocation() {
        return location;
    }

    public String getPath() {
        return PrettyURLEncoder.createContentUrl(getAssociation().getAssociationId(), title, alias);
    }

    public String getPath(boolean isAdminMode) {
        Association a = getAssociation();

        if (alias != null && alias.startsWith("/") && a.getAssociationtype() == AssociationType.DEFAULT_POSTING_FOR_SITE) {
            // Alias brukes når angitt og man har en hovedknytning og man ikke er i adminmodus
            if (isAdminMode) {
                return getPath();
            } else {
                return alias;
            }
        }
        return getPath();
    }

    public String getPath(HttpServletRequest request) {
        boolean isAdminMode = HttpHelper.isAdminMode(request);
        return getPath(isAdminMode);
    }

    /**
     * @deprecated Use "Aksess.getContextPath() + getPath()"
     */
    @Deprecated
    public String getUrl() {
        return Aksess.getContextPath() + getPath();
    }

    /**
     * @deprecated Use "Aksess.getContextPath() + getPath(boolean isAdminMode)"
     */
    @Deprecated
    public String getUrl(boolean isAdminMode) {
        return Aksess.getContextPath() + getPath(isAdminMode);
    }

    /**
     * @deprecated Use "Aksess.getContextPath() + getPath(HttpServletRequest request)"
     */
    @Deprecated
    public String getUrl(HttpServletRequest request) {
        return Aksess.getContextPath() + getPath(request);
    }

    /**
     * Aliases are user specified URLs
     * eg http://www.site.com/news/

     * Alias always starts and ends with /
     * Alias / is used for frontpage
     */
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

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
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

    public ExpireAction getExpireAction() {
        return expireAction;
    }

    public void setExpireAction(ExpireAction expireAction) {
        this.expireAction = expireAction;
    }

    /**
     * @param attributeDataType AttributeDataType.CONTENT_DATA, AttributeDataType.ANY or AttributeDataType.META_DATA
     * @return Attributes of given type.
     */
    public List<Attribute> getAttributes(AttributeDataType attributeDataType) {
        if (attributeDataType == AttributeDataType.CONTENT_DATA) {
            return contentAttributes;
        } else {
            return metaAttributes;
        }
    }

    public Map<String, Attribute> getContentAttributes() {
        Map<String, Attribute> map = new HashMap<>();
        if (contentAttributes != null) {
            for (Attribute a : contentAttributes) {
                map.put(a.getName(), a);
            }
        }
        return map;
    }

    public Map<String, Attribute> getMetaAttributes() {
        Map<String, Attribute> map = new HashMap<>();
        if (metaAttributes != null) {
            for (Attribute a : metaAttributes) {
                map.put(a.getName(), a);
            }
        }
        return map;
    }


    public void setAttributes(List<Attribute> attr, AttributeDataType attributeDataType) {
        if (attributeDataType == CONTENT_DATA) {
            contentAttributes = attr;
        } else {
            metaAttributes = attr;
        }
        indexAttributes();
    }

    /**
     * @param name of the attribute wanting value for
     * @return the String value of the attribute or empty string.
     */
    public String getAttributeValue(String name) {
        Attribute attr = getAttribute(name);
        if (attr != null) {
            String value = attr.getValue();
            if (value == null) {
                return "";
            } else {
                return value;
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

    /**
     * @param name the attribute name as specified in the content template xml.
     * @return the Attribute of type <code>AttributeDataType.CONTENT_DATA</code> or null.
     */
    public Attribute getAttribute(String name) {
        return getAttribute(name, AttributeDataType.CONTENT_DATA);
    }

    /**
     * @param name the attribute name as specified in the content template xml.
     * @param attributeDataType type, either <code>AttributeDataType.CONTENT_DATA</code> or <code>AttributeDataType.META_DATA</code>
     *             or <code>AttributeDataType.ANY</code>
     * @return the Attribute or null.
     */
    public Attribute getAttribute(String name, AttributeDataType attributeDataType) {
        if (isBlank(name)) {
            throw new IllegalArgumentException("Name was blank");
        }
        String attributeName = type + name.toLowerCase();
        Attribute cached = attributeIndex.get(attributeName);
        if(cached != null) {
            return cached;
        }

        List<Attribute> list = getAttributeList(name, attributeDataType);

        if (name.contains("[") && name.contains("].")) {
            name = name.substring(name.indexOf("].") + 2, name.length());
        }

        for (Attribute attr : list) {
            if (attr.getName().equalsIgnoreCase(name)) {
                attributeIndex.put(attributeName, attr);
                return attr;
            }
        }
        return null;
    }

    private List<Attribute> getAttributeList(String name, AttributeDataType attributeDataType) {
        List<Attribute> list = getAttributesByType(attributeDataType);

        if (name.contains("[") && name.contains("].")) {
            String rowStr = name.substring(name.indexOf("[") + 1, name.indexOf("]"));
            int row = Integer.parseInt(rowStr, 10);

            name = name.substring(0, name.indexOf("["));

            for (Attribute attr : list) {
                if (attr.getName().equalsIgnoreCase(name) && attr instanceof RepeaterAttribute) {
                    RepeaterAttribute repeaterAttribute = (RepeaterAttribute)attr;
                    if (row < repeaterAttribute.getNumberOfRows()) {
                        return repeaterAttribute.getRow(row);
                    }
                }
            }
            return new ArrayList<>();
        }


        return list;
    }

    private List<Attribute> getAttributesByType(AttributeDataType attributeDataType) {
        List<Attribute> list;
        switch (attributeDataType) {
            case CONTENT_DATA:
                list = contentAttributes;
                break;
            case META_DATA:
                list = metaAttributes;
                break;
            default:
                list = new ArrayList<>();
                list.addAll(contentAttributes);
                list.addAll(metaAttributes);
                break;
        }
        return list;
    }

    public void addAttribute(Attribute attr, AttributeDataType attributeDataType) {
        Attribute a = getAttribute(attr.getName(), attributeDataType);
        if (a != null) {
            throw new IllegalArgumentException("Attribute " + attr.getName() + " already exists for content with id: " + getId());
        }

        if (attributeDataType == AttributeDataType.CONTENT_DATA) {
            contentAttributes.add(attr);
        } else {
            metaAttributes.add(attr);
        }
        attributeIndex.put(attributeIndexKey(attr, attributeDataType), attr);
    }

    public void removeAttribute(String name, AttributeDataType attributeDataType) {
        List<Attribute> list;
        if (attributeDataType == AttributeDataType.CONTENT_DATA) {
            list = contentAttributes;
        } else {
            list = metaAttributes;
        }

        for (int i = 0; i < list.size(); i++) {
            Attribute attr = list.get(i);
            if (attr.getName().equalsIgnoreCase(name)) {
                list.remove(attr);
                attributeIndex.remove(attributeIndexKey(attr, attributeDataType));
                break;
            }
        }
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void addAttachment(Attachment attachment) {
        attachments.add(attachment);
    }

    public List<Multimedia> getMultimedia() {
        return multimedia;
    }

    public void addMultimedia(Multimedia m) {
        multimedia.add(m);
    }


    public List<Topic> getTopics() {
        return topics;
    }


    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }


    public void addTopic(Topic topic) {
        if (topics == null) {
            topics = new ArrayList<>();
        }
        boolean found = false;
        for (Topic t : topics) {
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

    /**
     * Person owning the content
     */
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

    public float getRatingScore() {
        return ratingScore;
    }

    public void setRatingScore(float ratingScore) {
        this.ratingScore = ratingScore;
    }

    public int getNumberOfRatings() {
        return numberOfRatings;
    }

    public void setNumberOfRatings(int numberOfRatings) {
        this.numberOfRatings = numberOfRatings;
    }

    public boolean isSearchable() {
        return isSearchable;
    }

    public void setSearchable(boolean searchable) {
        this.isSearchable = searchable;
    }

    public boolean isMinorChange() {
        return isMinorChange;
    }

    public void setMinorChange(boolean minorChange) {
        isMinorChange = minorChange;
    }

    public Date getChangeFromDate() {
        return changeFromDate;
    }

    public void setChangeFromDate(Date changeFromDate) {
        this.changeFromDate = changeFromDate;
    }

    public int getNumberOfComments() {
        return numberOfComments;
    }

    public void setNumberOfComments(int numberOfComments) {
        this.numberOfComments = numberOfComments;
    }

    public Date getLastMajorChange() {
        return lastMajorChange;
    }

    public void setLastMajorChange(Date lastMajorChange) {
        this.lastMajorChange = lastMajorChange;
    }

    public String getLastMajorChangeBy() {
        return lastMajorChangeBy;
    }

    public void setLastMajorChangeBy(String lastMajorChangeBy) {
        if (lastMajorChangeBy != null && lastMajorChangeBy.length() > 64) {
            lastMajorChangeBy = lastMajorChangeBy.substring(0, 63);
        }
        this.lastMajorChangeBy = lastMajorChangeBy;
    }

    public Hearing getHearing() {
        return hearing;
    }

    public void setHearing(Hearing hearing) {
        this.hearing = hearing;
    }

    public void doForEachAttribute(AttributeDataType attributeDataType, AttributeHandler handler) {
        List<Attribute> attributes = getAttributes(attributeDataType);
        for (Attribute attribute : attributes) {
            doForAttribute(handler, attribute);
        }
    }

    public void doForAttribute(AttributeHandler handler, Attribute attribute) {
        if (attribute instanceof RepeaterAttribute) {
            RepeaterAttribute repeater = (RepeaterAttribute)attribute;
            int rows = repeater.getNumberOfRows();
            for (int rowNo = 0; rowNo < rows; rowNo ++) {
                List<Attribute> attributes = repeater.getRow(rowNo);
                for (Attribute childAttribute : attributes) {
                    doForAttribute(handler, childAttribute);
                }
            }
        } else {
            handler.handleAttribute(attribute);
        }
    }

    public boolean attributesAreUpdatedFromTemplate() {
        return attributesAreUpdatedFromTemplate;
    }

    public void setAttributesAreUpdatedFromTemplate(boolean attributesAreUpdatedFromTemplate) {
        this.attributesAreUpdatedFromTemplate = attributesAreUpdatedFromTemplate;
    }

    @Override
    public String toString() {
        return "Content{" +
                "type=" + type +
                ", contentTemplateId=" + contentTemplateId +
                ", metaDataTemplateId=" + metaDataTemplateId +
                ", displayTemplateId=" + displayTemplateId +
                ", documentTypeId=" + documentTypeId +
                ", documentTypeIdForChildren=" + documentTypeIdForChildren +
                ", groupId=" + groupId +
                ", creator='" + creator + '\'' +
                ", owner='" + owner + '\'' +
                ", ownerPerson='" + ownerPerson + '\'' +
                ", location='" + location + '\'' +
                ", openInNewWindow=" + openInNewWindow +
                ", alias='" + alias + '\'' +
                ", createDate=" + createDate +
                ", publishDate=" + publishDate +
                ", expireDate=" + expireDate +
                ", revisionDate=" + revisionDate +
                ", changeFromDate=" + changeFromDate +
                ", expireAction=" + expireAction +
                ", visibilityStatus=" + visibilityStatus +
                ", numberOfNotes=" + numberOfNotes +
                ", versionId=" + versionId +
                ", version=" + version +
                ", status=" + status +
                ", language=" + language +
                ", title='" + title + '\'' +
                ", altTitle='" + altTitle + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", keywords='" + keywords + '\'' +
                ", publisher='" + publisher + '\'' +
                ", lastModified=" + lastModified +
                ", modifiedBy='" + modifiedBy + '\'' +
                ", approvedBy='" + approvedBy + '\'' +
                ", changeDescription='" + changeDescription + '\'' +
                ", forumId=" + forumId +
                ", associations=" + associations +
                ", contentAttributes=" + contentAttributes +
                ", metaAttributes=" + metaAttributes +
                ", attachments=" + attachments +
                ", multimedia=" + multimedia +
                ", topics=" + topics +
                ", isModified=" + isModified +
                ", isCheckedOut=" + isCheckedOut +
                ", isLocked=" + isLocked +
                ", isSearchable=" + isSearchable +
                ", isMinorChange=" + isMinorChange +
                ", lastMajorChange=" + lastMajorChange +
                ", lastMajorChangeBy='" + lastMajorChangeBy + '\'' +
                ", ratingScore=" + ratingScore +
                ", numberOfRatings=" + numberOfRatings +
                ", numberOfComments=" + numberOfComments +
                ", hearing=" + hearing +
                ", attributesAreUpdatedFromTemplate=" + attributesAreUpdatedFromTemplate +
                '}';
    }

    public void indexAttributes() {
        Map<String, Attribute> contentAttributeIndex = new HashMap<>();
        Map<String, Attribute> contentAttributes = getContentAttributes();
        for (Attribute entry : contentAttributes.values()) {
            contentAttributeIndex.put(attributeIndexKey(entry, AttributeDataType.CONTENT_DATA), entry);
        }

        Map<String, Attribute> metaAttributes = getMetaAttributes();
        for (Attribute entry : metaAttributes.values()) {
            contentAttributeIndex.put(attributeIndexKey(entry, AttributeDataType.META_DATA), entry);
        }
        attributeIndex = contentAttributeIndex;
    }

    private String attributeIndexKey(Attribute attribute, AttributeDataType type) {
        return type + attribute.getName().toLowerCase();
    }
}
