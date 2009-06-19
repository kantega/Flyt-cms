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

import java.util.Map;
import java.util.HashMap;

/**
 * Author: Kristian Lier Selnæs, Kantega
 * Date: 20.des.2006
 * Time: 13:40:48
 */
public class RssRequestHandler implements Controller {


    /**
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map model = new HashMap();
        RequestParameters params = new RequestParameters(request);

        int parentId = params.getInt("thisId");
        int max = (params.getInt("max")!= -1)? params.getInt("max") : 20;
        model.put("max", new Integer(max));

        if(parentId != -1){
            ContentQuery query = new ContentQuery();
            ContentIdentifier parent = new ContentIdentifier();
            parent.setAssociationId(parentId);
            query.setAssociatedId(parent);

            model.put("contentQuery", query);
            model.put("baseUrl", URLHelper.getServerURL(request));
            
        }else {
            request.getRequestDispatcher("/404.jsp").forward(request, response);
        }

        return new ModelAndView("/WEB-INF/jsp/rss/rss.jsp", model);
    }
}
