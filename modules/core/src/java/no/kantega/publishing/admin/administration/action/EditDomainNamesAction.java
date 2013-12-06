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

package no.kantega.publishing.admin.administration.action;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.ao.HostnamesDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditDomainNamesAction extends AbstractController {

    private String view;

    private HostnamesDao dao;
    private SiteCache siteCache;

    @Autowired
    public EditDomainNamesAction(HostnamesDao dao, SiteCache siteCache) {
        this.dao = dao;
        this.siteCache = siteCache;
    }

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        RequestParameters param = new RequestParameters(request, "utf-8");

        Map<String, Object> model = new HashMap<>();

        int siteId = param.getInt("siteId");

        if (request.getMethod().equalsIgnoreCase("POST")) {
            List<String> hostnames = new ArrayList<String>();
            for (int i = 0; i < 40; i++) {
                String hostname = param.getString("hostname" + i);
                if (hostname != null) {
                    hostname = hostname.trim();
                    if (hostname.length() > 0) {
                        hostname = hostname.replaceAll("http://", "");
                        hostname = hostname.replaceAll("https://", "");
                        hostnames.add(hostname);
                    }
                }
            }

            // Save hostnames in db
            dao.setHostnamesForSiteId(siteId, hostnames);
            siteCache.reloadCache();
            return new ModelAndView(new RedirectView("ListSites.action"));
        } else {
            Site site = siteCache.getSiteById(siteId);
            model.put("site", site);

            return new ModelAndView(view, model);
        }

    }

    public void setView(String view) {
        this.view = view;
    }
}

