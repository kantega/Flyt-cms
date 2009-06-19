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

package no.kantega.publishing.admin.ajax;

import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.commons.client.util.RequestParameters;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * User: Anders Skar, Kantega AS
 * Date: May 2, 2008
 * Time: 10:46:32 AM
 */
public class SearchFilenamesAsXMLAction implements ServletContextAware, Controller {
    private ServletContext context;

    /**
     * Henter ut filnavn i applikasjonen for å kunne gjøre autocomplete
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map model = new HashMap();
        RequestParameters param = new RequestParameters(request);

        String name = param.getString("value");
        if (name != null && name.length() >= 3) {
            String startDir = "/";
            if (name.indexOf("/") != -1) {
                startDir = name.substring(0, name.lastIndexOf("/"));
            }
            SecuritySession securitySession = SecuritySession.getInstance(request);
            if (securitySession.isUserInRole(Aksess.getAdminRole())) {
                Set files = context.getResourcePaths(startDir);
                List filenames = new ArrayList();
                Iterator it = files.iterator();
                while (it.hasNext()) {
                    String filename = (String)it.next();
                    if (filename.endsWith("/") || filename.endsWith(".jsp")) {
                        filenames.add(filename);
                    }
                }

                // Sorter filnavn
                Collections.sort(filenames);

                model.put("filenames", filenames);
            }
        }

        return new ModelAndView("/WEB-INF/jsp/ajax/searchresult-files.jsp", model);
    }

    public void setServletContext(ServletContext servletContext) {
        this.context = servletContext;
    }
}
