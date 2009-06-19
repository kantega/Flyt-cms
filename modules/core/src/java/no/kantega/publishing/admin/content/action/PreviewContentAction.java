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

package no.kantega.publishing.admin.content.action;

import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.util.RequestHelper;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.Site;
import no.kantega.publishing.common.data.DisplayTemplate;
import no.kantega.publishing.common.cache.SiteCache;
import no.kantega.publishing.common.cache.DisplayTemplateCache;
import no.kantega.publishing.common.data.enums.ContentType;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.io.IOException;

public class PreviewContentAction extends HttpServlet {
    private static String SOURCE = "aksess.PreviewContentAction";
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String template = Aksess.getStartPage();

        HttpSession session = request.getSession(true);
        Content current = (Content)session.getAttribute("currentContent");


        try {
            if (current != null) {
                RequestHelper.setRequestAttributes(request, current);

                int siteId = current.getAssociation().getSiteId();
                Site site = SiteCache.getSiteById(siteId);
                String alias = site.getAlias();

                if (current.getType() == ContentType.PAGE) {
                    DisplayTemplate dt = null;
                    try {
                        dt = DisplayTemplateCache.getTemplateById(current.getDisplayTemplateId());
                    } catch (SystemException e) {
                        Log.error(SOURCE, e, null, null);
                    }
                    if (dt != null) {
                        RequestHelper.runTemplateControllers(dt, request, response, getServletContext());
                        template = dt.getView();
                        // Dersom malnavn inneholder $SITE, erstatt med riktig site
                        if (template.indexOf("$SITE") != -1) {
                            template = template.replaceAll("\\$SITE", alias.substring(0, alias.length() - 1));
                        }
                    }

                } else {
                    template = "/admin/showcontentinframe.jsp";
                }
            }
        } catch (SystemException e) {
            Log.error(SOURCE, e, null, null);
        } catch (Exception e) {
            throw new ServletException(e);
        }

        request.getRequestDispatcher(template).forward(request, response);
    }
}
