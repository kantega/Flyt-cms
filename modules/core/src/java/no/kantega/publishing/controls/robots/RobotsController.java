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

package no.kantega.publishing.controls.robots;

import no.kantega.commons.util.URLHelper;
import no.kantega.publishing.api.configuration.SystemConfiguration;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentQuery;
import no.kantega.publishing.common.service.ContentManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;


/**
 * Controller for generating robots.txt
 */
public class RobotsController implements Controller {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private SystemConfiguration configuration;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean crawlerSiteMapEnabled = configuration.getBoolean("crawler.sitemap.enabled", false);
        ServletOutputStream out = response.getOutputStream();

        // Se om det finnes en robots.txt fil på rota.
        // Hvis det gjør det, skriver vi ut innholdet av denne.
        try (InputStream is = request.getServletContext().getResourceAsStream("/robots.txt")){
            try {
                if (is != null) {
                    try(BufferedReader rd = new BufferedReader(new InputStreamReader(is))) {
                        while (rd.ready()) {
                            out.println(rd.readLine());
                        }
                    }
                }
            } catch (IOException e) {
                log.error(" Exception while reading robots.txt: " + e.getMessage(), e);
            }
        }

        // Hvis generering av sitemap.xml for crawlere er enabled,
        // legger vi til en linje som sier hvor denne filen ligger.
        if (crawlerSiteMapEnabled) {
            out.println("Sitemap: " + URLHelper.getRootURL(request) + "sitemap.xml");
        }

        ContentQuery query = new ContentQuery();
        query.setSql("content.IsSearchable = 0");
        ContentManagementService cms = new ContentManagementService(request);
        List<Content> excludedPages = cms.getContentSummaryList(query);
        for (Content excludedPage : excludedPages) {
            if (excludedPage.getAlias() != null && excludedPage.getAlias().length() > 0) {
                out.println("Disallow: " + request.getContextPath() + excludedPage.getAlias());
            } else {
                out.println("Disallow: " + excludedPage.getUrl());
            }
        }

        String[] disallowUrls = configuration.getStrings("robots.disallow");
        if (disallowUrls != null) {
            for (String disallowUrl : disallowUrls) {
                out.println("Disallow: " + disallowUrl);
            }
        }

        out.println("Disallow: /admin/");
        out.println("Disallow: /login/");
        out.println("Disallow: /Login.action");
        out.println("Disallow: /RequestPasswordReset.action");
        out.println("Disallow: /ResetPassword.action");
        return null;
    }
}
