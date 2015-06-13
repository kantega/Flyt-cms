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

import com.google.common.collect.Maps;
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

    private static Map<Integer, DisplayTemplate> displaytemplates = new HashMap<>();
    private static Date lastUpdate = null;

    public static DisplayTemplate getTemplateById(int id) throws SystemException {
        if (shouldUpdate()) {
            reloadCache();
        }
        return displaytemplates.get(id);
    }

    public static DisplayTemplate getTemplateByPublicId(String id) {
        if (shouldUpdate()) {
            reloadCache();
        }

        for (Map.Entry<Integer, DisplayTemplate> entry : displaytemplates.entrySet()) {
            DisplayTemplate template = entry.getValue();
            if (id != null && id.equalsIgnoreCase(template.getPublicId())) {
                return template;
            }
        }
        return null;
    }

    private static boolean shouldUpdate() {
        return lastUpdate == null || TemplateConfigurationCache.getInstance().getLastUpdate().getTime() > lastUpdate.getTime();
    }

    public static synchronized void reloadCache() throws SystemException {
        log.debug( "Loading cache");

        List<DisplayTemplate> dtlist = TemplateConfigurationCache.getInstance().getTemplateConfiguration().getDisplayTemplates();
        Map<Integer, DisplayTemplate> newdisplaytemplates = Maps.newHashMapWithExpectedSize(dtlist.size());
        lastUpdate  = new Date();
        for (DisplayTemplate template : dtlist) {
            newdisplaytemplates.put(template.getId(), template);
        }
        displaytemplates = newdisplaytemplates;
    }

    public static List<DisplayTemplate> getTemplates() throws SystemException {
        return TemplateConfigurationCache.getInstance().getTemplateConfiguration().getDisplayTemplates();
    }

}
