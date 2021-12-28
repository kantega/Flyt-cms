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

import no.kantega.publishing.api.content.ContentStatus;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.enums.ContentType;
import no.kantega.publishing.common.data.enums.ObjectType;
import no.kantega.publishing.common.util.PrettyURLEncoder;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public class SiteMapEntry extends NavigationMapEntry {
    private int uniqueId = -1;
    private String alias = null;
    private int visibilityStatus = -1;
    private int numberOfNotes = 0;
    private int associationCategory = 0;
    private int contentId = -1;
    private int groupId = -1;
    private boolean openInNewWindow = false;
    private Date lastModified = null;
    private String owner = null;
    private String ownerPerson = null;
    public ContentType type = ContentType.PAGE;
    private boolean isSearchable = true;
    private String altTitle = null;
    private int contentTemplateId;
    private int displayTemplateId;

    public SiteMapEntry() {
    }

    @Deprecated
    public SiteMapEntry(int uniqueId, int currentId, int parentId, ContentType type, ContentStatus status, int visibilityStatus, String title, int numberOfNotes, int contentTemplateId) {
        this.uniqueId = uniqueId;
        this.currentId = currentId;
        this.parentId  = parentId;
        this.type = type;
        this.status = status;
        this.visibilityStatus = visibilityStatus;
        this.title = title;
        this.numberOfNotes = numberOfNotes;
        this.contentTemplateId = contentTemplateId;
    }


    public String getUrl() {
        return Aksess.getContextPath() + PrettyURLEncoder.createContentUrl(currentId, title, alias);
    }

    public String getAlias() {
        return alias;
    }

    public int getVisibilityStatus() {
        return visibilityStatus;
    }

    public ContentStatus getStatus() {
        return status;
    }

    public void setUniqueId(int uniqueId) {
        this.uniqueId = uniqueId;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sjekker om denne SiteMapEntry er forfedre for en SiteMapEntry med angitte childId
     * @param childId
     * @return true hvis denne SiteMapEntry er forfedre for den SiteMapEntry med angitte childId
     */

     public boolean isAncestorFor(int childId){
        boolean result = false;
        if (getChildren() != null) {
            for (int i=0;i<getChildren().size(); i++) {
                SiteMapEntry child = (SiteMapEntry) getChildren().get(i);
                if (child.getId() == childId) return true;
                result = child.isAncestorFor(childId);
                if (result) break;
            }
        }
        return result;
    }

    public void setNumberOfNotes(int numberOfNotes) {
        this.numberOfNotes = numberOfNotes;
    }

    public int getNumberOfNotes() {
        return numberOfNotes;
    }

    public boolean isOpenInNewWindow() {
        return openInNewWindow;
    }

    public void setOpenInNewWindow(boolean openInNewWindow) {
        this.openInNewWindow = openInNewWindow;
    }

    public String getTarget() {
        if (openInNewWindow) {
            return "_blank";
        }

        return "";
    }

    @Deprecated
    public String getTarget(HttpServletRequest request) {
        return getTarget();
    }

    public int getAssociationCategory() {
        return associationCategory;
    }

    public void setAssociationCategory(int associationCategory) {
        this.associationCategory = associationCategory;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public int getObjectType() {
        return ObjectType.ASSOCIATION;
    }

    public String getName() {
        return title;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwnerPerson() {
        return ownerPerson;
    }

    public void setOwnerPerson(String ownerPerson) {
        this.ownerPerson = ownerPerson;
    }

    public ContentType getType() {
        return type;
    }

    public boolean isSearchable() {
        return isSearchable;
    }

    public void setSearchable(boolean searchable) {
        isSearchable = searchable;
    }

    public int getContentTemplateId() {
        return contentTemplateId;
    }

    public void setContentTemplateId(int contentTemplateId) {
        this.contentTemplateId = contentTemplateId;
    }

    public int getDisplayTemplateId() {
        return displayTemplateId;
    }

    public void setDisplayTemplateId(int displayTemplateId) {
        this.displayTemplateId = displayTemplateId;
    }

    public String getAltTitle() {
        return altTitle;
    }

    public void setAltTitle(String altTitle) {
        this.altTitle = altTitle;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public void setType(ContentType type) {
        this.type = type;
    }

    public void setStatus(ContentStatus status) {
        this.status = status;
    }

    public void setVisibilityStatus(int visibilityStatus) {
        this.visibilityStatus = visibilityStatus;
    }

    public void setCurrentId(int currentId) {
        this.currentId = currentId;
    }
}
