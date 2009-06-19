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

package no.kantega.publishing.admin.content.action;

import no.kantega.publishing.common.exception.ExceptionHandler;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.service.TopicMapService;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.security.data.Role;
import no.kantega.commons.log.Log;
import no.kantega.commons.client.util.RequestParameters;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.io.IOException;


public class DeleteRoleTopicAction   extends HttpServlet {
    private static String SOURCE = "aksess.DeleteRoleTopicAction";

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        RequestParameters param = new RequestParameters(request);

        Topic topic = (Topic)session.getAttribute("currentTopic");
        String roleId = param.getString("roleId");
        if (topic != null && roleId != null) {
            try {
                TopicMapService topicService = new TopicMapService(request);

                Role role = new Role();
                role.setId(roleId);

                topicService.removeTopicSIDAssociation(topic, role);

                response.sendRedirect("topic.jsp");
            } catch (Exception e) {
                Log.error(SOURCE, e, null, null);

                ExceptionHandler handler = new ExceptionHandler();
                handler.setThrowable(e, SOURCE);
                request.getSession(true).setAttribute("handler", handler);
                request.getRequestDispatcher(Aksess.ERROR_URL).forward(request, response);
            }
        }
    }
}
