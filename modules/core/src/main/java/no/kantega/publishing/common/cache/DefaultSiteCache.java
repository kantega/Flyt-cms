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

import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.hostname.HostnamesDao;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.Aksess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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


        Configuration c = Aksess.getConfiguration();
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
        no.kantega.publishing.api.model.Site defaultSite;
        if (sites.size() > 1) {
            defaultSite = determineDefaultSite();
        } else if(sites.size() == 1){
            defaultSite = sites.get(0);
        } else {
            throw new IllegalStateException("No sites configured in aksesstemplate-config.xml");
        }
        return defaultSite;
    }

    private Site determineDefaultSite() {
        Collection<Site> defaultSites = sites.stream()
                .filter(Site::isDefault)
                .collect(Collectors.toList());
        int size = defaultSites.size();
        if(size > 1){
            throw new IllegalStateException(size + " default sites exists, only 1 permitted. Add isDefault=\"true\" to only one site in aksess-templateconfig.xml");
        }
        Site defaultSite;
        if (size == 1) {
            defaultSite = defaultSites.iterator().next();
        } else {
            defaultSite = sites.get(0);
        }
        return defaultSite;
    }

    public void setTemplateConfigurationCache(TemplateConfigurationCache templateConfigurationCache) {
        this.templateConfigurationCache = templateConfigurationCache;
    }

    public void setHostnamesDao(HostnamesDao hostnamesDao) {
        this.hostnamesDao = hostnamesDao;
    }
}
