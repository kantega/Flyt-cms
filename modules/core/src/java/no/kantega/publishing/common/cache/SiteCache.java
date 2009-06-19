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

import no.kantega.publishing.common.ao.HostnamesDao;
import no.kantega.publishing.common.data.Site;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.spring.RootContext;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.exception.ConfigurationException;
import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.log.Log;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;

/**
 * @deprecated Use no.kantega.publishing.api.cache.SiteCache with Dependency Injection instead
 */
public class SiteCache {

    public static Site getSiteByHostname(String hostname) throws SystemException {
        return (Site) getInstance().getSiteByHostname(hostname);
    }

    public static Site getSiteById(int siteId) throws SystemException {
        return (Site) getInstance().getSiteById(siteId);
    }

    public static Site getSiteByPublicIdOrAlias(String id) throws SystemException {
        return (Site) getInstance().getSiteByPublicIdOrAlias(id);
    }

    public static Site getSiteByAlias(String id) throws SystemException {
        return (Site) getInstance().getSiteByPublicIdOrAlias(id);
    }

    public static List<Site> getSites() throws SystemException {
        return (List) getInstance().getSites();
    }


    public static void reloadCache() throws SystemException {
        getInstance().reloadCache();
    }

    private static no.kantega.publishing.api.cache.SiteCache getInstance() {
        ApplicationContext context = RootContext.getInstance();
        Map beans = context.getBeansOfType(no.kantega.publishing.api.cache.SiteCache.class);
        return (no.kantega.publishing.api.cache.SiteCache) beans.values().iterator().next();
    }

}
