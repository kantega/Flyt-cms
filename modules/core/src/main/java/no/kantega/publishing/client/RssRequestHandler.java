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

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.util.URLHelper;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentQuery;
import no.kantega.publishing.common.data.DisplayTemplate;
import no.kantega.publishing.common.service.ContentManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class RssRequestHandler implements Controller {
    private static final Logger log = LoggerFactory.getLogger(RssRequestHandler.class);

    /**
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<>();
        RequestParameters params = new RequestParameters(request);

        int parentId = params.getInt("thisId");
        int max = (params.getInt("max")!= -1)? params.getInt("max") : 20;
        model.put("max", max);

        String view = "/WEB-INF/jsp/rss/rss.jsp";
        if(parentId != -1){
            ContentManagementService cms = new ContentManagementService(request);

            ContentIdentifier parent =  ContentIdentifier.fromAssociationId(parentId);

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
            log.info("Serving rss for Content with parent " + parent);
        } else {
            request.getRequestDispatcher("/404.jsp").forward(request, response);
        }

        return new ModelAndView(view, model);
    }
}
