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

import no.kantega.publishing.common.data.enums.ContentType;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.api.model.PublicIdObject;

import java.util.List;
import java.util.ArrayList;

/**
 *
 */
public class ContentTemplate implements PublicIdObject {

    private DocumentType documentType;
    private DocumentType documentTypeForChildren;
    private ContentType contentType = ContentType.PAGE;

    private String name;
    private String templateFile;

    private Integer expireMonths;
    private Integer expireAction;

    private boolean isHearingEnabled = false;
    private Integer keepVersions;

    List<ContentTemplate> allowedParentTemplates;
    List<AssociationCategory> associationCategories;
    private AssociationCategory defaultAssociationCategory;

    private int id = -1;
    private String publicId = "";

    public ContentType getContentType() {
        if (contentType == null) {
            return ContentType.PAGE;
        } else {
            return contentType;
        }
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public DocumentType getDocumentTypeForChildren() {
        return documentTypeForChildren;
    }

    public void setDocumentTypeIdForChildren(DocumentType documentTypeForChildren) {
        this.documentTypeForChildren = documentTypeForChildren;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(String templateFile) {
        this.templateFile = templateFile;
    }

    public Integer getExpireMonths() {
        return expireMonths;
    }

    public void setExpireMonths(Integer expireMonths) {
        this.expireMonths = expireMonths;
    }

    public Integer getExpireAction() {
        return expireAction;
    }

    public void setExpireAction(int expireAction) {
        this.expireAction = expireAction;
    }

    public List<AssociationCategory> getAssociationCategories() {
        if (associationCategories == null) {
            associationCategories = new ArrayList<AssociationCategory>();
        }
        return associationCategories;
    }

    public void setAssociationCategories(List<AssociationCategory> associationCategories) {
        this.associationCategories = associationCategories;
    }

    public List<ContentTemplate> getAllowedParentTemplates() {
        if (allowedParentTemplates == null) {
            allowedParentTemplates = new ArrayList<ContentTemplate>();
        }
        return allowedParentTemplates;
    }

    public void setAllowedParentTemplates(List<ContentTemplate> allowedParentTemplates) {
        this.allowedParentTemplates = allowedParentTemplates;
    }

    public boolean isHearingEnabled() {
        return isHearingEnabled;
    }

    public void setHearingEnabled(boolean hearingEnabled) {
        this.isHearingEnabled = hearingEnabled;
    }

    public Integer getKeepVersions() {
        return keepVersions;
    }

    public int computeKeepVersions() {
        int keepVersions;
        if(getKeepVersions() == null) {
            keepVersions = Aksess.getHistoryMaxVersions();
        } else if(getKeepVersions() == -1) {
            keepVersions = -1;
        } else {
            keepVersions = getKeepVersions();
        }
        return keepVersions;
    }

    public void setKeepVersions(Integer keepVersions) {
        this.keepVersions = keepVersions;
    }


    public AssociationCategory getDefaultAssociationCategory() {
        return defaultAssociationCategory;
    }

    public void setDefaultAssociationCategory(AssociationCategory defaultAssociationCategory) {
        this.defaultAssociationCategory = defaultAssociationCategory;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String toString() {
        String str = "";

        if (publicId != null && publicId.length() > 0) {
            str = publicId;
        }

        if (id != -1) {
            str = str + "(" + id + ")";
        }

        return str;
    }
}
