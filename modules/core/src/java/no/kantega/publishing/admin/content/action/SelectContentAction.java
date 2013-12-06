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

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.common.data.Content;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class SelectContentAction implements Controller {
    private String view;


    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<>();

        RequestParameters param = new RequestParameters(request);
        if (param.getBoolean("selectContentId", false)) {
            model.put("selectContentId", Boolean.TRUE);
        }

        model.put("multiple", param.getBoolean("multiple", false));

        int currentId = -1;
        Content currentContent = (Content)request.getSession().getAttribute(AdminSessionAttributes.CURRENT_EDIT_CONTENT);
        if (currentContent != null) {
            if (currentContent.isNew()) {
                currentId = currentContent.getAssociation().getParentAssociationId();
            } else {
                currentId = currentContent.getAssociation().getAssociationId();
            }
        }
        model.put("currentId", currentId);
        model.put("startId", param.getInt("startId"));


        return new ModelAndView(view, model);
    }

    public void setView(String view) {
        this.view = view;
    }
}
