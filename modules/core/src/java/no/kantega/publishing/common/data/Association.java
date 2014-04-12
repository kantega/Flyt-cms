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

import no.kantega.publishing.api.model.BaseObject;
import no.kantega.publishing.common.data.enums.AssociationType;
import no.kantega.publishing.common.data.enums.ObjectType;

import java.util.StringTokenizer;

/**
 * Object representing an association for a Content object.
 */
public class Association extends BaseObject {
    private int associationId = -1;
    private int contentId = -1; // 
    private int parentAssociationId = -1;
    private AssociationCategory category = null;
    private int siteId = 1;
    private int associationtype = AssociationType.DEFAULT_POSTING_FOR_SITE;
    private int priority = -1;
    private String name = "";
    private String path = "";
    private int depth = 0;
    private boolean isCurrent = false;
    private boolean isDeleted = false;
    private int numberOfViews = 0;

    public Association() {
    }

    public int getObjectType() {
        return ObjectType.ASSOCIATION;
    }

    public int getAssociationId() {
        return associationId;
    }

    public void setAssociationId(int associationId) {
        this.associationId = associationId;
    }

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public int getParentAssociationId() {
        return parentAssociationId;
    }

    public void setParentAssociationId(int parentAssociationId) {
        this.parentAssociationId = parentAssociationId;
    }

    public AssociationCategory getCategory() {
        return category;
    }

    public void setCategory(AssociationCategory category) {
        this.category = category;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public int getAssociationtype() {
        return associationtype;
    }

    public void setAssociationtype(int associationtype) {
        this.associationtype = associationtype;
    }

    public int getPriority() {
        if(priority == -1){
            return (int)((System.currentTimeMillis())/1000);
        }
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return null;
    }

    public String getOwnerPerson() {
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int[] getPathElementIds() {
        if (path == null) {
            return new int[0];
        }

        StringTokenizer tokens = new StringTokenizer(path, "/");
        int ints[] = new int[tokens.countTokens()];
        int i = 0;
        while (tokens.hasMoreTokens()) {
            String tmp = tokens.nextToken();
            ints[i++] = Integer.parseInt(tmp);
        }
        return ints;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public int getNumberOfViews() {
        return numberOfViews;
    }

    public void setNumberOfViews(int numberOfViews) {
        this.numberOfViews = numberOfViews;
    }

    @Override
    public String toString() {
        return String.format("associationId: %s\ncontentId: %s\nparentAssociationId: %s\nsiteId: %s\nassociationType: %s",
        associationId,contentId,parentAssociationId,siteId,associationtype);
    }
}
