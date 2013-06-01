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

import no.kantega.publishing.api.model.PublicIdObject;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.enums.ContentType;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * A template specifying what attributes should be available for a particular Content object.
 */
public class ContentTemplate implements PublicIdObject {

    private DocumentType documentType;
    private DocumentType documentTypeForChildren;
    private ContentType contentType = ContentType.PAGE;

    private String name;
    private String templateFile;

    // This needs to be a Boolean, not boolean, else if will get a default value of false after serialization
    private Boolean isSearchable = Boolean.TRUE;

    private Boolean isDefaultSearchable = Boolean.TRUE;
    private String defaultPageUrlAlias = null;

    private Integer expireMonths;
    private Integer expireAction;

    private boolean isHearingEnabled = false;
    private Integer keepVersions;

    List<ContentTemplate> allowedParentTemplates;
    List<AssociationCategory> associationCategories;
    private AssociationCategory defaultAssociationCategory;

    private int id = -1;
    private String publicId = "";

    private String helptext;

    private List<Element> attributeElements;
    private List<Element> propertyElements;

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
            associationCategories = new ArrayList<>();
        }
        return associationCategories;
    }

    public void setAssociationCategories(List<AssociationCategory> associationCategories) {
        this.associationCategories = associationCategories;
    }

    public List<ContentTemplate> getAllowedParentTemplates() {
        if (allowedParentTemplates == null) {
            allowedParentTemplates = new ArrayList<>();
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

    public String getHelptext() {
        return helptext;
    }

    public void setHelptext(String helptext) {
        this.helptext = helptext;
    }

    public List<Element> getAttributeElements() {
        if (attributeElements == null) {
            attributeElements = new ArrayList<Element>();
        }
        return attributeElements;
    }

    public void setAttributeElements(List<Element> attributeElements) {
        this.attributeElements = attributeElements;
    }

    public List<Element> getPropertyElements() {
        if (propertyElements == null) {
            propertyElements = new ArrayList<Element>();
        }
        return propertyElements;
    }

    public void setPropertyElements(List<Element> propertyElements) {
        this.propertyElements = propertyElements;
    }

    /**
     * @return whether content of this <code>ContentTemplate</code> should be searchable.
     */
    public boolean isSearchable() {
        // Since templates are created with reflection fields are not instantiated properly.
        return isSearchable == null || isSearchable;
    }

    public void setSearchable(boolean searchable) {
        this.isSearchable = searchable;
    }

    /**
     * @return whether content of this <code>ContentTemplate</code> should be searchable by default.
     * If set to false the <code>Content</code> object has to be set searchable in the admin interface.
     */
    public Boolean isDefaultSearchable() {
        // Since templates are created with reflection fields are not instantiated properly.
        return isDefaultSearchable == null || isDefaultSearchable;
    }

    public void setDefaultSearchable(Boolean defaultSearchable) {
        isDefaultSearchable = defaultSearchable;
    }

    public String getDefaultPageUrlAlias() {
        return defaultPageUrlAlias;
    }

    public void setDefaultPageUrlAlias(String defaultPageUrlAlias) {
        this.defaultPageUrlAlias = defaultPageUrlAlias;
    }
}
