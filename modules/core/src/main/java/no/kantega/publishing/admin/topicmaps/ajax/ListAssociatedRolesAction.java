/*
 * /*
 *  * Copyright 2009 Kantega AS
 *  *
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

package no.kantega.publishing.admin.topicmaps.ajax;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.service.TopicMapService;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.Role;
import no.kantega.publishing.topicmaps.data.Topic;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ListAssociatedRolesAction extends AbstractTopicInfoAction {
    @Override
    public ModelAndView handleTopicInfoRequest(HttpServletRequest request, HttpServletResponse response, Topic topic) {
        Map<String, Object> model = new HashMap<>();

        model.put("topic", topic);

        TopicMapService topicService = new TopicMapService(request);

        RequestParameters param = new RequestParameters(request);
        String deleteRoleId = param.getString("deleteId");
        if (deleteRoleId != null) {
            // Delete association between role and topic
            Role role = new Role();
            role.setId(deleteRoleId);

            topicService.removeTopicSIDAssociation(topic, role);
        }

        String addRoleId = param.getString("addId");
        if (addRoleId != null) {
            // Add association between role and topic
            Role role = new Role();
            role.setId(addRoleId);
            topicService.addTopicSIDAssociation(topic, role);
        }

        SecuritySession session = SecuritySession.getInstance(request);
        if (session.isUserInRole(Aksess.getAdminRole())) {
            model.put("canAdd", Boolean.TRUE);
            model.put("canDelete", Boolean.TRUE);
        }

        model.put("roles", topicService.getRolesByTopic(topic));

        model.put("allRoles", session.getAllRoles());

        return new ModelAndView(view, model);
    }
}

