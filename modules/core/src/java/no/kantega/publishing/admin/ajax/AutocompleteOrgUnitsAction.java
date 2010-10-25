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

import no.kantega.publishing.org.OrgUnit;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.spring.RootContext;
import no.kantega.publishing.org.OrganizationManager;
import no.kantega.commons.client.util.RequestParameters;

public class AutocompleteOrgUnitsAction implements Controller {
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();

        List<OrgUnit> orgUnits = new ArrayList<OrgUnit>();

        String name = request.getParameter("term");
        if (name != null && name.length() >= 3) {
            ApplicationContext context = RootContext.getInstance();
            Iterator i = context.getBeansOfType(OrganizationManager.class).values().iterator();
            if(i.hasNext()) {
                OrganizationManager manager = (OrganizationManager) i.next();
                orgUnits = manager.searchOrgUnits(name);
            }
        }
        model.put("organizations", orgUnits);

        return new ModelAndView("/WEB-INF/jsp/ajax/searchresult-organizations.jsp", model);
    }
}

