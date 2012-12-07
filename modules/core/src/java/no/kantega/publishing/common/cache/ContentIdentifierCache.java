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
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.ContentAO;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ContentIdentifiers cached by alias and site.
 */
public class ContentIdentifierCache {
    private static final String SOURCE = "aksess.ContentIdentifierCache";

    private final static Map<String, String> aliases = new ConcurrentHashMap<String, String>();
    private final static Map<String, Collection<ContentIdentifier>> contentIdentifiers = new ConcurrentHashMap<String, Collection<ContentIdentifier>>();
    private static Date lastUpdate = null;

    public static ContentIdentifier getContentIdentifierByAlias(int siteId, String name) throws SystemException {

        reloadCacheIfNecessary();

        Collection<ContentIdentifier> cids = fetchValuesFromCache(name);

        return getContentIdentifierForSite(siteId, cids);
    }

    private static ContentIdentifier getContentIdentifierForSite(int siteId, Collection<ContentIdentifier> cids) {
        ContentIdentifier contentIdentifier = null;
        if (cids != null && cids.size() > 0) {
            if (cids.size() == 1) {
                // Finnes kun en side med dette aliaset
                contentIdentifier =  cids.iterator().next();
            } else {
                // Finnes flere, returner den med riktig siteId
                for (ContentIdentifier tmp : cids) {
                    if (tmp.getSiteId() == siteId) {
                        contentIdentifier = tmp;
                        break;
                    }
                }
            }
        }
        return contentIdentifier;
    }

    private static Collection<ContentIdentifier> fetchValuesFromCache(String name) {
        Collection<ContentIdentifier> cids;
        name = name.toLowerCase();

        synchronized (contentIdentifiers) {
            cids = contentIdentifiers.get(name);
            if (cids == null) {
                // Prøv å hent alias med / tilslutt
                cids = contentIdentifiers.get(name + "/");

            }
        }
        return cids;
    }

    private static void reloadCacheIfNecessary() {
        if (contentIdentifiers.size() == 0) {
            Log.debug(SOURCE, "Loading cache", null, null);
            reloadCache();
            if (contentIdentifiers.size() == 0) {
                Log.info(SOURCE, "No aliases found in database. Database may be empty, or error.", null, null);
            }
        } else if ((lastUpdate == null) || (Aksess.getDatabaseCacheTimeout() > 0 && lastUpdate.getTime() + (Aksess.getDatabaseCacheTimeout()) < new Date().getTime())) {
            reloadCache();
        }
    }

    public static String getAliasByContentIdentifier(int siteId, int associationId) throws SystemException {
        if ((lastUpdate == null) || (Aksess.getDatabaseCacheTimeout() > 0 && lastUpdate.getTime() + (Aksess.getDatabaseCacheTimeout()) < new Date().getTime())) {
            reloadCache();
        }

        return aliases.get(getCacheKey(siteId, associationId));
    }

    private static String getCacheKey(int siteId, int associationId) {
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(siteId);
        keyBuilder.append('-');
        keyBuilder.append(associationId);
        return keyBuilder.toString();
    }


    public static void reloadCache() throws SystemException {
        Log.debug(SOURCE, "Loading cache", null, null);

        Map<String, Collection<ContentIdentifier>> newAliases = ContentAO.getContentIdentifiersMappedByAlias();

        synchronized (contentIdentifiers) {
            contentIdentifiers.clear();
            contentIdentifiers.putAll(newAliases);
        }

        synchronized (aliases) {
            aliases.clear();
            for (String alias : newAliases.keySet()) {
                Collection<ContentIdentifier> cids = newAliases.get(alias);
                for (ContentIdentifier cid : cids) {
                    String key = getCacheKey(cid.getSiteId(), cid.getAssociationId());
                    aliases.put(key, alias);
                }
            }
        }

        lastUpdate  = new Date();
    }

}
