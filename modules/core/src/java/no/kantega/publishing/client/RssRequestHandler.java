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

package no.kantega.publishing.client;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.util.URLHelper;
import no.kantega.publishing.common.data.ContentQuery;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.DisplayTemplate;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.service.ContentManagementService;

import java.util.Map;
import java.util.HashMap;

public class RssRequestHandler implements Controller {
    /**
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        RequestParameters params = new RequestParameters(request);

        String view = "/WEB-INF/jsp/rss/rss.jsp";
        int parentId = params.getInt("thisId");
        int max = (params.getInt("max")!= -1)? params.getInt("max") : 20;
        model.put("max", max);

        if(parentId != -1){
            ContentManagementService cms = new ContentManagementService(request);

            ContentIdentifier parent = new ContentIdentifier();
            parent.setAssociationId(parentId);

            Content content = cms.getContent(parent);
            if (content != null && content.getDisplayTemplateId() > 0) {
                DisplayTemplate template = cms.getDisplayTemplate(content.getDisplayTemplateId());
                if (template.getRssView() != null && template.getRssView().length() > 0) {
                    view = template.getRssView();
                }
            }

            ContentQuery query = new ContentQuery();
            query.setAssociatedId(parent);

            model.put("contentQuery", query);
            model.put("baseUrl", URLHelper.getServerURL(request));

        } else {
            request.getRequestDispatcher("/404.jsp").forward(request, response);
        }

        return new ModelAndView(view, model);
    }
}
