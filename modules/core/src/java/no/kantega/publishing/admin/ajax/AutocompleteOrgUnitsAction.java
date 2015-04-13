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
import no.kantega.publishing.org.OrganizationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutocompleteOrgUnitsAction implements Controller {

    @Autowired(required = false)
    private OrganizationManager<? extends OrgUnit> manager;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<>();

        List<? extends OrgUnit> orgUnits;

        String name = request.getParameter("term");
        if (manager != null && name != null && name.length() >= 3) {
            orgUnits = manager.searchOrgUnits(name);
        } else {
            orgUnits = Collections.emptyList();
        }
        model.put("organizations", orgUnits);

        return new ModelAndView("/WEB-INF/jsp/ajax/searchresult-organizations.jsp", model);
    }
}

