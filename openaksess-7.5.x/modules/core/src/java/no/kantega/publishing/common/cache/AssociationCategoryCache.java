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

package no.kantega.publishing.common.cache;

import no.kantega.publishing.common.data.AssociationCategory;
import no.kantega.commons.exception.SystemException;

import java.util.HashMap;
import java.util.List;
import java.util.Date;

public class AssociationCategoryCache {
    private static final HashMap<String, AssociationCategory> associationcategoriesByPublicId = new HashMap<String, AssociationCategory>();
    private static Date lastUpdate = null;

    public static AssociationCategory getAssociationCategoryById(int id) throws SystemException {
        List<AssociationCategory> associationCategories = TemplateConfigurationCache.getInstance().getTemplateConfiguration().getAssociationCategories();
        for (AssociationCategory category : associationCategories) {
            if (category.getId() == id) {
                return category;
            }
        }

        return null;
    }

    public static AssociationCategory getAssociationCategoryByPublicId(String id) throws SystemException {
        if (lastUpdate == null || TemplateConfigurationCache.getInstance().getLastUpdate().getTime() > lastUpdate.getTime()) {
            reloadCache();
        }

        synchronized (associationcategoriesByPublicId) {
            return associationcategoriesByPublicId.get(id.toLowerCase());
        }
    }

    public static void reloadCache() throws SystemException {
        List alist = TemplateConfigurationCache.getInstance().getTemplateConfiguration().getAssociationCategories();

        synchronized (associationcategoriesByPublicId) {
            associationcategoriesByPublicId.clear();
            for (int i = 0; i < alist.size(); i++) {
                AssociationCategory category = (AssociationCategory)alist.get(i);
                String id = category.getPublicId();
                if (id != null) {
                    associationcategoriesByPublicId.put(id.toLowerCase(), category);                    
                }
            }
        }
        lastUpdate = new Date();
    }
}
