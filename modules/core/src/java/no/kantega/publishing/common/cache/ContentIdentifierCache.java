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
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.common.data.ContentIdentifier;

import java.util.*;

/**
 * User: Anders Skar, Kantega AS
 * Date: Apr 4, 2007
 * Time: 11:15:49 AM
 */
public class ContentIdentifierCache {
    private static final String SOURCE = "aksess.ContentIdentifierCache";

    private static HashMap aliases = new HashMap();
    private static HashMap contentIdentifiers = new HashMap();
    private static Date lastUpdate = null;

    public static ContentIdentifier getContentIdentifierByAlias(int siteId, String name) throws SystemException {
        List cids = null;

        if (contentIdentifiers.size() == 0) {
            Log.debug(SOURCE, "Loading cache", null, null);
            reloadCache();
            if (contentIdentifiers.size() == 0) {
                Log.info(SOURCE, "No aliases found in database. Database may be empty, or error.", null, null);
            }
        } else if ((lastUpdate == null) || (Aksess.getDatabaseCacheTimeout() > 0 && lastUpdate.getTime() + (Aksess.getDatabaseCacheTimeout()) < new Date().getTime())) {
            reloadCache();
        }

        name = name.toLowerCase();

        synchronized (contentIdentifiers) {
            cids = (List) contentIdentifiers.get(name);
            if (cids == null) {
                // Prøv å hent alias med / tilslutt
                cids = (List) contentIdentifiers.get(name + "/");

            }
        }

        if (cids != null && cids.size() > 0) {
            if (cids.size() == 1) {
                // Finnes kun en side med dette aliaset
                return (ContentIdentifier)cids.get(0);
            }

            // Finnes flere, returner den med riktig siteId
            for (int i = 0; i < cids.size(); i++) {
                ContentIdentifier tmp = (ContentIdentifier) cids.get(i);
                if (tmp.getSiteId() == siteId) {
                    return tmp;
                }
            }

            return (ContentIdentifier)cids.get(0);
        }

        return null;
    }

    public static String getAliasByContentIdentifier(int siteId, int associationId) throws SystemException {
        if ((lastUpdate == null) || (Aksess.getDatabaseCacheTimeout() > 0 && lastUpdate.getTime() + (Aksess.getDatabaseCacheTimeout()) < new Date().getTime())) {
            reloadCache();
        }

        synchronized (aliases) {
            return (String) aliases.get("" + siteId + "-" + associationId);
        }
    }


    public static void reloadCache() throws SystemException {
        Log.debug(SOURCE, "Loading cache", null, null);

        Map newAliases = ContentAO.getContentIdentifierCacheValues();

        synchronized (contentIdentifiers) {
            contentIdentifiers.clear();
            Iterator it = newAliases.keySet().iterator();
            while (it.hasNext()) {
                String alias = (String)it.next();
                List cids = (List)newAliases.get(alias);
                contentIdentifiers.put(alias, cids);
            }
        }

        synchronized (aliases) {
            aliases.clear();
            Iterator it = newAliases.keySet().iterator();
            while (it.hasNext()) {
                String alias = (String)it.next();
                List cids = (List)newAliases.get(alias);
                for (int i = 0; i < cids.size(); i++) {
                    ContentIdentifier cid =  (ContentIdentifier)cids.get(i);
                    aliases.put("" + cid.getSiteId() + "-" + cid.getAssociationId(), alias);
                }
            }
        }

        lastUpdate  = new Date();
    }
}
