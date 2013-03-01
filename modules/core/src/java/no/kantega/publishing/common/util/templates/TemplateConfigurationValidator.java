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

package no.kantega.publishing.common.util.templates;

import no.kantega.publishing.common.data.*;
import no.kantega.publishing.api.model.PublicIdObject;

import java.util.*;

/**
 * User: Anders Skar, Kantega AS
 * Date: Jan 14, 2009
 * Time: 1:49:30 PM
 *
 */
public class TemplateConfigurationValidator {

    /**
     * Validates template configuration and updates references to templates, documenttypes etc in configuration
     * @param configuration - configuration to validate
     * @return - List of validationerrors
     */
    public List<TemplateConfigurationValidationError> validate(TemplateConfiguration configuration) {
        List<TemplateConfigurationValidationError> errors = new ArrayList<>();

        errors.addAll(validateSites(configuration));
        errors.addAll(validateDocumentTypes(configuration));
        errors.addAll(validateDisplayTemplates(configuration));
        errors.addAll(validateContentTemplates(configuration));
        errors.addAll(validateMetadataTemplates(configuration));
        errors.addAll(validateAssociationCategories(configuration));

        return errors;
    }

    private List<TemplateConfigurationValidationError> validateAssociationCategories(TemplateConfiguration configuration) {
        List<TemplateConfigurationValidationError> errors = new ArrayList<TemplateConfigurationValidationError>();

        List duplicates = getDuplicates(configuration.getAssociationCategories());
        for (int i = 0; i < duplicates.size(); i++) {
            AssociationCategory category = (AssociationCategory)duplicates.get(i);
            errors.add(new TemplateConfigurationValidationError(category.getName(), "aksess.templateconfig.error.duplicateid", category.toString()));
        }
        return errors;
    }

    private List<TemplateConfigurationValidationError> validateSites(TemplateConfiguration configuration) {
        List<TemplateConfigurationValidationError> errors = new ArrayList<TemplateConfigurationValidationError>();

        for (Site site : configuration.getSites()) {
            if (site.getId() < 0) {
                errors.add(new TemplateConfigurationValidationError(site.getName(), "aksess.templateconfig.error.missingdatabaseid", "" + site.getId()));
            }
        }

        return errors;
    }

    private List<TemplateConfigurationValidationError> validateDocumentTypes(TemplateConfiguration configuration) {
        List<TemplateConfigurationValidationError> errors = new ArrayList<TemplateConfigurationValidationError>();

        List duplicates = getDuplicates(configuration.getDocumentTypes());
        for (int i = 0; i < duplicates.size(); i++) {
            DocumentType dt = (DocumentType)duplicates.get(i);
            errors.add(new TemplateConfigurationValidationError(dt.getName(), "aksess.templateconfig.error.duplicateid", dt.toString()));
        }
        
        for (DocumentType dt : configuration.getDocumentTypes()) {
            if (dt.getId() < 0) {
                errors.add(new TemplateConfigurationValidationError(dt.getName(), "aksess.templateconfig.error.missingdatabaseid", "" + dt.getId()));
            }
        }

        return errors;
    }

    private List<TemplateConfigurationValidationError> validateDisplayTemplates(TemplateConfiguration configuration) {
        List<TemplateConfigurationValidationError> errors = new ArrayList<TemplateConfigurationValidationError>();

        List duplicates = getDuplicates(configuration.getDisplayTemplates());
        for (int i = 0; i < duplicates.size(); i++) {
            DisplayTemplate dt = (DisplayTemplate)duplicates.get(i);
            errors.add(new TemplateConfigurationValidationError(dt.getName(), "aksess.templateconfig.error.duplicateid", dt.toString()));
        }

        for (DisplayTemplate displayTemplate : configuration.getDisplayTemplates()) {
            if (displayTemplate.getId() < 0) {
                errors.add(new TemplateConfigurationValidationError(displayTemplate.getName(), "aksess.templateconfig.error.missingdatabaseid", "" + displayTemplate.getId()));
            }

            if (!objectExistsUpdateId(configuration.getContentTemplates(),  displayTemplate.getContentTemplate())) {
                errors.add(new TemplateConfigurationValidationError(displayTemplate.getName(), "aksess.templateconfig.error.invalidreferencetocontenttemplate", "" + displayTemplate.getContentTemplate()));
            }

            if (!objectExistsUpdateId(configuration.getMetadataTemplates(),  displayTemplate.getMetaDataTemplate())) {
                errors.add(new TemplateConfigurationValidationError(displayTemplate.getName(), "aksess.templateconfig.error.invalidreferencetocontenttemplate", "" + displayTemplate.getMetaDataTemplate()));
            }


            // Check if referenced sites are found
            for (Site allowedSite : displayTemplate.getSites()) {
                if (!objectExistsUpdateId(configuration.getSites(), allowedSite)) {
                    errors.add(new TemplateConfigurationValidationError(displayTemplate.getName(), "aksess.templateconfig.error.invalidreferencetosite", "" + allowedSite));
                }
            }
        }

        return errors;
    }

    private List<TemplateConfigurationValidationError> validateContentTemplates(TemplateConfiguration configuration) {
        List<TemplateConfigurationValidationError> errors = new ArrayList<TemplateConfigurationValidationError>();

        List duplicates = getDuplicates(configuration.getContentTemplates());
        for (int i = 0; i < duplicates.size(); i++) {
            ContentTemplate ct = (ContentTemplate)duplicates.get(i);
            errors.add(new TemplateConfigurationValidationError(ct.getName(), "aksess.templateconfig.error.duplicateid", ct.toString()));
        }

        for (ContentTemplate contentTemplate : configuration.getContentTemplates()) {
            if (contentTemplate.getId() < 0) {
                errors.add(new TemplateConfigurationValidationError(contentTemplate.getName(), "aksess.templateconfig.error.missingdatabaseid", "" + contentTemplate.getId()));
            }

            // Check reference to document type
            if (contentTemplate.getDocumentType() != null) {
                if (!objectExistsUpdateId(configuration.getDocumentTypes(), contentTemplate.getDocumentType())) {
                    errors.add(new TemplateConfigurationValidationError(contentTemplate.getName(), "aksess.templateconfig.error.invalidreferencetodocumenttype", "" + contentTemplate.getDocumentType()));
                }
            }

            if (contentTemplate.getDocumentTypeForChildren() != null) {
                if (!objectExistsUpdateId(configuration.getDocumentTypes(), contentTemplate.getDocumentTypeForChildren())) {
                    errors.add(new TemplateConfigurationValidationError(contentTemplate.getName(), "aksess.templateconfig.error.invalidreferencetodocumenttype", "" + contentTemplate.getDocumentTypeForChildren()));
                }
            }

            // Check reference to associationcategories
            for (AssociationCategory templateAssociationCategory : contentTemplate.getAssociationCategories()) {
                if (!objectExistsUpdateId(configuration.getAssociationCategories(), templateAssociationCategory)) {
                    errors.add(new TemplateConfigurationValidationError(contentTemplate.getName(), "aksess.templateconfig.error.invalidreferencetoassociationcategory", "" + templateAssociationCategory));
                }
            }

            if (!objectExistsUpdateId(configuration.getAssociationCategories(), contentTemplate.getDefaultAssociationCategory())) {
                errors.add(new TemplateConfigurationValidationError(contentTemplate.getName(), "aksess.templateconfig.error.invalidreferencetoassociationcategory", "" + contentTemplate.getDefaultAssociationCategory()));
            }


            // Check reference to parenttemplates
            if (contentTemplate.getAllowedParentTemplates() != null) {
                for (ContentTemplate parentTemplate : contentTemplate.getAllowedParentTemplates()) {
                    if (!objectExistsUpdateId(configuration.getContentTemplates(), parentTemplate)) {
                        errors.add(new TemplateConfigurationValidationError(contentTemplate.getName(), "aksess.templateconfig.error.invalidreferencetocontenttemplate", "" + parentTemplate));
                    }
                }
            }
        }

        return errors;
    }

    private List<TemplateConfigurationValidationError> validateMetadataTemplates(TemplateConfiguration configuration) {
        List<TemplateConfigurationValidationError> errors = new ArrayList<TemplateConfigurationValidationError>();

        List duplicates = getDuplicates(configuration.getMetadataTemplates());
        for (int i = 0; i < duplicates.size(); i++) {
            ContentTemplate ct = (ContentTemplate)duplicates.get(i);
            errors.add(new TemplateConfigurationValidationError(ct.getName(), "aksess.templateconfig.error.duplicateid", ct.toString()));
        }

        for (ContentTemplate contentTemplate : configuration.getMetadataTemplates()) {
            if (contentTemplate.getId() < 0) {
                errors.add(new TemplateConfigurationValidationError(contentTemplate.getName(), "aksess.templateconfig.error.missingdatabaseid", "" + contentTemplate.getId()));
            }
        }

        return errors;
    }

    private boolean objectExistsUpdateId(List objects, PublicIdObject object) {
        if (object == null) {
            return true;
        }

        for (int i = 0; i < objects.size(); i++) {
            PublicIdObject o = (PublicIdObject)objects.get(i);
            if (o.getId() == object.getId()) {
                return true;
            }

            if (object.getPublicId() != null) {
                if (o.getPublicId().equalsIgnoreCase(object.getPublicId())) {
                    object.setId(o.getId());
                    return true;
                }
            }
        }
        return false;
    }


    private List<PublicIdObject> getDuplicates(List objects) {
        List<PublicIdObject> duplicates = new ArrayList<PublicIdObject>();

        Map<Integer, PublicIdObject> ids = new HashMap<Integer, PublicIdObject>();
        Map<String, PublicIdObject> publicIds = new HashMap<String, PublicIdObject>();
        
        for (int i = 0; i < objects.size(); i++) {
            boolean isDuplicate = false;
            PublicIdObject o = (PublicIdObject)objects.get(i);
            if (ids.containsKey(o.getId())) {
                duplicates.add(o);
                isDuplicate = true;
            } else {
                ids.put(o.getId(), o);
            }

            if (o.getPublicId() != null && o.getPublicId().length() > 0) {
                if (publicIds.containsKey(o.getPublicId())) {
                    if (!isDuplicate) {
                        duplicates.add(o);
                    }
                } else {
                    publicIds.put(o.getPublicId(), o);
                }
            }
        }
        return duplicates;
    }
}
