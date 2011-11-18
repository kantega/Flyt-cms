/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.admin.administration.action;

import no.kantega.publishing.admin.viewcontroller.AdminController;
import no.kantega.publishing.common.data.ContentQuery;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ListAliasesAction extends AdminController {
    private String view;
    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();

        ContentQuery query = new ContentQuery();
        query.setShowExpired(true);
        String driver = dbConnectionFactory.getDriverName().toLowerCase();
        if (driver.contains("oracle")) {
            query.setSql(" and content.Alias is not null and associations.Type = 1");
        } else {
            query.setSql(" and content.Alias is not null and content.Alias <> '' and associations.Type = 1");
        }

        model.put("query", query);

        return new ModelAndView(view, model);
    }

    public void setView(String view) {
        this.view = view;
    }
}
