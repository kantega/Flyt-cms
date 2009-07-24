/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.common.util.templates;

import no.kantega.publishing.common.data.AssociationCategory;
import no.kantega.publishing.common.data.ContentTemplate;
import no.kantega.publishing.common.data.enums.ContentType;
import no.kantega.publishing.common.exception.ChildContentNotAllowedException;
import no.kantega.publishing.common.cache.TemplateConfigurationCache;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class AssociationCategoryHelper {
    private TemplateConfigurationCache templateConfigurationCache;

    public AssociationCategoryHelper(TemplateConfigurationCache templateConfigurationCache) {
        this.templateConfigurationCache = templateConfigurationCache;
    }

    public List<AssociationCategory> getAllowedAssociationCategories(ContentTemplate parentTemplate) throws ChildContentNotAllowedException {
        List<AssociationCategory> tmpAllowedAssociations = parentTemplate.getAssociationCategories();
        if (tmpAllowedAssociations == null || tmpAllowedAssociations.size() == 0) {
            throw new ChildContentNotAllowedException();
        } else if (parentTemplate.getContentType() != ContentType.PAGE) {
            throw new ChildContentNotAllowedException();
        }

        // Template only holds id of AssociationCategory, get complete AssociationCategory from cache
        List<AssociationCategory> allAssociations = templateConfigurationCache.getTemplateConfiguration().getAssociationCategories();
        List<AssociationCategory> allowedAssociations = new ArrayList<AssociationCategory>();
        for (AssociationCategory allowedAssociation : tmpAllowedAssociations) {
            for (AssociationCategory allAssociation : allAssociations) {
                if (allAssociation.getId() == allowedAssociation.getId()) {
                    allowedAssociations.add(allAssociation);
                }
            }
        }
        return allowedAssociations;
    }

}
