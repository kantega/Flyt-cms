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

import no.kantega.publishing.api.model.Site;

import java.util.ArrayList;
import java.util.List;

/**
 * Object containing the current Template configuration
 */
public class TemplateConfiguration {
    List<Site> sites = new ArrayList<>();
    List<AssociationCategory> associationCategories = new ArrayList<>();
    List<DocumentType> documentTypes = new ArrayList<>();
    List<ContentTemplate> contentTemplates = new ArrayList<>();
    List<ContentTemplate> metadataTemplates = new ArrayList<>();
    List<DisplayTemplate> displayTemplates = new ArrayList<>();

    public List<Site> getSites() {
        return sites;
    }

    public void setSites(List<Site> sites) {
        this.sites = sites;
    }

    public List<AssociationCategory> getAssociationCategories() {
        return associationCategories;
    }

    public void setAssociationCategories(List<AssociationCategory> associationCategories) {
        this.associationCategories = associationCategories;
    }

    public List<DocumentType> getDocumentTypes() {
        return documentTypes;
    }

    public void setDocumentTypes(List<DocumentType> documentTypes) {
        this.documentTypes = documentTypes;
    }
    
    public List<ContentTemplate> getContentTemplates() {
        return contentTemplates;
    }

    public void setContentTemplates(List<ContentTemplate> contentTemplates) {
        this.contentTemplates = contentTemplates;
    }

    public List<DisplayTemplate> getDisplayTemplates() {
        return displayTemplates;
    }

    public void setDisplayTemplates(List<DisplayTemplate> displayTemplates) {
        this.displayTemplates = displayTemplates;
    }

    public List<ContentTemplate> getMetadataTemplates() {
        return metadataTemplates;
    }

    public void setMetadataTemplates(List<ContentTemplate> metadataTemplates) {
        this.metadataTemplates = metadataTemplates;
    }
}
