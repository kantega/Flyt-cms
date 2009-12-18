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

package no.kantega.publishing.admin.content.action;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.commons.log.Log;
import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.client.util.RequestParameters;

import java.util.HashMap;
import java.util.Map;

/**
 * Dialogue which allows user to update display period (publish and expire date) for a page
 */
public class ViewDisplayPeriodAction implements Controller {
    private String view;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        ContentManagementService cms = new ContentManagementService(request);
        RequestParameters param = new RequestParameters(request);
        int id = param.getInt("id");
        ContentIdentifier cid = new ContentIdentifier();
        cid.setAssociationId(id);
        Content content = cms.getContent(cid, false);
        if (content != null) {
            model.put("content", content);
        }
        return new ModelAndView(view, model);
    }

    public void setView(String view) {
        this.view = view;
    }
}
