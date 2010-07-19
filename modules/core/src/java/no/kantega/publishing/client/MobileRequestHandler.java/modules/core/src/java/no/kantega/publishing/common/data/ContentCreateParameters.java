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

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.cache.AssociationCategoryCache;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Anders Skar, Kantega AS
 * Date: Nov 18, 2008
 * Time: 10:45:51 AM
 */
public class ContentCreateParameters {
    private int displayTemplateId = -1;
    private int contentTemplateId = -1;
    private int mainParentId = -1;
    private int[] parentIds = null;
    private int categoryId = -1;
    private Map<String, String> defaultValues = new HashMap<String, String>();

    public ContentCreateParameters() {
        
    }

    public ContentCreateParameters(HttpServletRequest request) throws SystemException {
        RequestParameters param = new RequestParameters(request);

        mainParentId = param.getInt("parentId");
        displayTemplateId = param.getInt("templateId");
        contentTemplateId = param.getInt("contentTemplateId");

        categoryId = param.getInt("categoryId");
        if(categoryId == -1) {
            String categoryName = param.getString("categoryName");
            if(categoryName != null && categoryName.length() > 0) {
                AssociationCategory associationCategory = AssociationCategoryCache.getAssociationCategoryByPublicId(categoryName);
                if(associationCategory != null) {
                    categoryId = associationCategory.getId();
                }
            }
        }
        
        Map<String, String> paramMap = param.getParametersAsMap();
        paramMap.remove("parentId");
        paramMap.remove("templateId");
        paramMap.remove("contentTemplateId");
        paramMap.remove("categoryId");
        paramMap.remove("categoryName");
        defaultValues.putAll(paramMap);


    }

    public int getDisplayTemplateId() {
        return displayTemplateId;
    }

    public void setDisplayTemplateId(int displayTemplateId) {
        this.displayTemplateId = displayTemplateId;
    }

    public int getContentTemplateId() {
        return contentTemplateId;
    }

    public void setContentTemplateId(int contentTemplateId) {
        this.contentTemplateId = contentTemplateId;
    }

    public int getMainParentId() {
        return mainParentId;
    }

    public void setMainParentId(int mainParentId) {
        this.mainParentId = mainParentId;
    }

    public int[] getParentIds() {
        return parentIds;
    }

    public void setParentIds(int[] parentIds) {
        this.parentIds = parentIds;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int category) {
        this.categoryId = category;
    }

    public Map<String, String> getDefaultValues() {
        return defaultValues;
    }

    public void setDefaultValues(Map<String, String> defaultValues) {
        this.defaultValues = defaultValues;
    }
}
