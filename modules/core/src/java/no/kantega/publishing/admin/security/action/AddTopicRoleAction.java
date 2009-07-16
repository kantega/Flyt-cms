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

package no.kantega.publishing.admin.security.action;

import no.kantega.publishing.security.data.SecurityIdentifier;
import no.kantega.publishing.security.data.User;
import no.kantega.publishing.security.data.Role;
import no.kantega.publishing.security.data.enums.RoleType;
import no.kantega.publishing.common.service.TopicMapService;
import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.commons.client.util.RequestParameters;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.HashMap;

import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.ModelAndView;

public class AddTopicRoleAction extends AbstractController {
    private String view;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request, "utf-8");

        String roletype = param.getString("roletype");
        String[] roles = param.getStrings("role");

        HttpSession session = request.getSession();
        Topic topic = (Topic)session.getAttribute("currentTopic");

        if (topic != null) {
            TopicMapService topicService = new TopicMapService(request);

            for (int i = 0; i < roles.length; i++) {
                String role = roles[i];

                // Add role / user
                SecurityIdentifier newRole;
                if (RoleType.USER.equalsIgnoreCase(roletype)) {
                    newRole = new User();
                } else {
                    newRole = new Role();
                }
                newRole.setId(role);

                topicService.addTopicSIDAssociation(topic, newRole);
            }
        }

        Map model = new HashMap();
        model.put("reloadWindow", Boolean.TRUE);

        return new ModelAndView(view, model);
    }

    public void setView(String view) {
        this.view = view;
    }
}