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
import no.kantega.commons.exception.ConfigurationException;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.HostnamesDao;
import no.kantega.publishing.common.data.Site;
import no.kantega.publishing.spring.RootContext;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultSiteCache implements no.kantega.publishing.api.cache.SiteCache {
    private static String SOURCE = "SiteCache";
    private List sites = null;
    private HashMap hostnames = null;
    private TemplateConfigurationCache templateConfigurationCache;
    private HostnamesDao hostnamesDao;

    public Site getSiteByHostname(String hostname) throws SystemException {
        if (hostnames == null) {
            reloadCache();
        }
        Site s = (Site)hostnames.get(hostname);
        if (s == null) {
            if (sites.size() > 0) {
                s = (Site)sites.get(0);
            } else {
                // Returnerer en tom default site dersom ikke definert noe enda
                s = new Site();
                s.setId(1);
                s.setName("No name");
                s.setAlias("/");
                return s;
            }
        }

        return s;
    }

    public Site getSiteById(int siteId) throws SystemException {
        if (sites == null) {
            reloadCache();
        }

        for (int i = 0; i < sites.size(); i++) {
            Site site = (Site)sites.get(i);
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

        for (int i = 0; i < sites.size(); i++) {
            Site site = (Site)sites.get(i);
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
        try {
            c = Aksess.getConfiguration();
            // Get hostnames from database and store in hashmap

            hostnames = new HashMap();
            for (int s = 0; s < sites.size(); s++) {
                Site site = (Site)sites.get(s);
                // Get hostnames from database
                List siteHostnames = hostnamesDao.getHostnamesForSiteId(site.getId());
                site.setHostnames(siteHostnames);

                // Insert into hashmap
                for (int h = 0; h < siteHostnames.size(); h++) {
                    String host = (String)siteHostnames.get(h);
                    hostnames.put(host, site);
                }

                // Check if site is disabled
                String alias = site.getAlias().toLowerCase();
                alias = alias.replace('/', '.');

                boolean isDisabled = c.getBoolean("site" + alias + "disabled", false);
                if (isDisabled) {
                    Log.info(SOURCE, "Slår av site:" + site.getName(), null, null);
                }
                site.setIsDisabled(isDisabled);

                String scheme = c.getString("site" + alias + "scheme", null);
                site.setScheme(scheme);
            }
        } catch (ConfigurationException e) {
            throw new SystemException("Configuration error", "", e);
        }

    }

    public static DefaultSiteCache getInstance() {
        ApplicationContext context = RootContext.getInstance();
        Map beans = context.getBeansOfType(DefaultSiteCache.class);
        return (DefaultSiteCache) beans.values().iterator().next();
    }

    public void setTemplateConfigurationCache(TemplateConfigurationCache templateConfigurationCache) {
        this.templateConfigurationCache = templateConfigurationCache;
    }

    public void setHostnamesDao(HostnamesDao hostnamesDao) {
        this.hostnamesDao = hostnamesDao;
    }
}