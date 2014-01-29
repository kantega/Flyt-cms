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

package no.kantega.useradmin.controls;

import no.kantega.publishing.common.Aksess;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.HashMap;

import no.kantega.security.api.search.SearchResult;
import no.kantega.security.api.role.RoleManager;
import no.kantega.useradmin.model.RoleManagementConfiguration;
import no.kantega.commons.client.util.RequestParameters;

/**
 * User: Anders Skar, Kantega AS
 * Date: Jun 26, 2007
 * Time: 2:35:56 PM
 */
public class SearchRolesController extends AbstractUserAdminController {

    public ModelAndView doHandleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request);

        String domain = param.getString("domain");
        String query  = param.getString("q");

        Map model = new HashMap();

        RoleManagementConfiguration config = getRoleConfiguration(domain);
        if (config != null) {
            model.put("domain", config.getDomain());
            model.put("roleConfigurations", getRoleConfiguration());
            model.put("numRoleConfigurations", getRoleConfiguration().size());
            RoleManager manager = config.getRoleManager();

            if (config.getRoleUpdateManager() != null) {
                model.put("canEdit", Boolean.TRUE);
            }

            if (query != null && query.length() > 2) {
                SearchResult result = manager.searchRoles(query);
                model.put("roles", result.getAllResults());
                model.put("query", query);
            } else {
                model.put("roles", manager.getAllRoles());
            }
        }

        model.put("adminRole", Aksess.getAdminRole());

        return new ModelAndView("role/search", model);
    }
}

