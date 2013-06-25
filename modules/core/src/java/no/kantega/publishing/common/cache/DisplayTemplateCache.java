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
import no.kantega.publishing.common.data.DisplayTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisplayTemplateCache {
    private static final Logger log = LoggerFactory.getLogger(DisplayTemplateCache.class);

    private static final HashMap<Integer, DisplayTemplate> displaytemplates = new HashMap<Integer, DisplayTemplate>();
    private static Date lastUpdate = null;

    public static DisplayTemplate getTemplateById(int id) throws SystemException {
        if (lastUpdate == null || TemplateConfigurationCache.getInstance().getLastUpdate().getTime() > lastUpdate.getTime()) {
            reloadCache();
        }
        synchronized (displaytemplates) {
            return displaytemplates.get(id);
        }
    }

    public static DisplayTemplate getTemplateByPublicId(String id) {
        if (lastUpdate == null || TemplateConfigurationCache.getInstance().getLastUpdate().getTime() > lastUpdate.getTime()) {
            reloadCache();
        }
        
        for (Object o : displaytemplates.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            DisplayTemplate template = (DisplayTemplate) entry.getValue();
            if (id != null && id.equalsIgnoreCase(template.getPublicId())) {
                return template;
            }
        }
        return null;
    }

    public static synchronized void reloadCache() throws SystemException {
        log.debug( "Loading cache");

        List dtlist = TemplateConfigurationCache.getInstance().getTemplateConfiguration().getDisplayTemplates();

        synchronized (displaytemplates) {
            lastUpdate  = new Date();
            displaytemplates.clear();
            for (int i = 0; i < dtlist.size(); i++) {
                DisplayTemplate template = (DisplayTemplate)dtlist.get(i);
                displaytemplates.put(template.getId(), template);
            }
        }
    }

    public static List<DisplayTemplate> getTemplates() throws SystemException {
        return TemplateConfigurationCache.getInstance().getTemplateConfiguration().getDisplayTemplates();
    }

}
