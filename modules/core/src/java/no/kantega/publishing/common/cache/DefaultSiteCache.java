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

import com.google.common.base.Predicate;
import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.HostnamesDao;
import no.kantega.publishing.spring.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Collections2.filter;

public class DefaultSiteCache implements no.kantega.publishing.api.cache.SiteCache {
    private static final Logger log = LoggerFactory.getLogger(DefaultSiteCache.class);

    private List<Site> sites = null;
    private Map<String, Site> hostnames = null;
    private TemplateConfigurationCache templateConfigurationCache;
    private HostnamesDao hostnamesDao;

    public Site getSiteByHostname(String hostname) throws SystemException {
        if (hostnames == null) {
            reloadCache();
        }
        return hostnames.get(hostname);
    }

    public Site getSiteById(int siteId) throws SystemException {
        if (sites == null) {
            reloadCache();
        }

        for (Site site : sites) {
            if (siteId == site.getId()) {
                return site;
            }
        }
        return null;
    }

    public Site getSiteByPublicIdOrAlias(String id) throws SystemException {
        if (sites == null) {
            reloadCache();
        }

        if (id.charAt(0) == '/') {
            if (id.charAt(id.length() - 1) != '/') id = id + "/";
        }

        for (Site site : sites) {
            if (id.equalsIgnoreCase(site.getPublicId()) || id.equalsIgnoreCase(site.getAlias())) {
                return site;
            }
        }
        return null;
    }

    public List<no.kantega.publishing.api.model.Site> getSites() throws SystemException {
        if (sites == null) {
            reloadCache();
        }
        return sites;
    }


    public void reloadCache() throws SystemException {
        // Get site config from XML
        sites = templateConfigurationCache.getTemplateConfiguration().getSites();


        Configuration c;
        c = Aksess.getConfiguration();
        // Get hostnames from database and store in hashmap

        hostnames = new HashMap<>();
        for (Site site : sites) {
            // Get hostnames from database
            List<String> siteHostnames = hostnamesDao.getHostnamesForSiteId(site.getId());
            site.setHostnames(siteHostnames);

            // Insert into hashmap
            for (String host : siteHostnames) {
                hostnames.put(host, site);
            }

            // Check if site is disabled
            String alias = site.getAlias().toLowerCase();
            alias = alias.replace('/', '.');

            boolean isDisabled = c.getBoolean("site" + alias + "disabled", false);
            if (isDisabled) {
                log.info( "Disabling site:" + site.getName());
            }
            site.setDisabled(isDisabled);

            String scheme = c.getString("site" + alias + "scheme", null);
            site.setScheme(scheme);
        }

    }

    @Override
    public no.kantega.publishing.api.model.Site getDefaultSite() {
        Collection<Site> defaultSites = filter(sites, new Predicate<Site>() {
            @Override
            public boolean apply(Site o) {
                return o.isDefault();
            }
        });
        int size = defaultSites.size();
        if(size != 1){
            throw new IllegalStateException(size + " default sites exists, only 1 permitted");
        }
        return defaultSites.iterator().next();
    }

    public static DefaultSiteCache getInstance() {
        ApplicationContext context = RootContext.getInstance();
        Map<String, DefaultSiteCache> beans = context.getBeansOfType(DefaultSiteCache.class);
        return beans.values().iterator().next();
    }

    public void setTemplateConfigurationCache(TemplateConfigurationCache templateConfigurationCache) {
        this.templateConfigurationCache = templateConfigurationCache;
    }

    public void setHostnamesDao(HostnamesDao hostnamesDao) {
        this.hostnamesDao = hostnamesDao;
    }
}
