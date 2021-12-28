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

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.data.ContentTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Anders Skar, Kantega AS
 * Date: Jan 5, 2009
 * Time: 3:21:53 PM
 */
public class MetadataTemplateCache {
    private static final HashMap templates = new HashMap();
    private static Date lastUpdate = null;

    public static ContentTemplate getTemplateById(int id) throws SystemException {
        return getTemplateById(id, false);
    }

    public static ContentTemplate getTemplateById(int id, boolean updateFromFile) throws SystemException {
        if (lastUpdate == null || TemplateConfigurationCache.getInstance().getLastUpdate().getTime() > lastUpdate.getTime()) {
            reloadCache();
        }
        synchronized (templates) {
            ContentTemplate template = (ContentTemplate) templates.get("" + id);
            if (template != null && updateFromFile) {
                TemplateConfigurationCache.getInstance().updateContentTemplateFromFile(template);
            }
            return template;
        }
    }

    public static ContentTemplate getTemplateByPublicId(String id) {
        if (lastUpdate == null || TemplateConfigurationCache.getInstance().getLastUpdate().getTime() > lastUpdate.getTime()) {
            reloadCache();
        }

        for (Object o : templates.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            ContentTemplate template = (ContentTemplate) entry.getValue();
            if (id != null && id.equalsIgnoreCase(template.getPublicId())) {
                return template;
            }
        }
        return null;
    }
   
    public static synchronized void reloadCache() throws SystemException {
        List listtemplates = getTemplates();

        synchronized (templates) {
            lastUpdate  = new Date();
            templates.clear();
            for (int i = 0; i < listtemplates.size(); i++) {
                ContentTemplate template = (ContentTemplate)listtemplates.get(i);
                templates.put("" + template.getId(), template);
            }
        }
    }

    public static List<ContentTemplate> getTemplates() throws SystemException {
        return TemplateConfigurationCache.getInstance().getTemplateConfiguration().getMetadataTemplates();
    }

}

