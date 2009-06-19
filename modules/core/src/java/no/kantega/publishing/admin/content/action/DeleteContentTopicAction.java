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

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.exception.ExceptionHandler;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.service.TopicMapService;
import no.kantega.publishing.topicmaps.data.Topic;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;

public class DeleteContentTopicAction  extends HttpServlet {
    private static String SOURCE = "aksess.DeleteContentTopicAction";

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestParameters param = new RequestParameters(request, "utf-8");

        HttpSession session = request.getSession();

        int contentId = param.getInt("contentId");
        Content content = (Content)session.getAttribute("currentContent");
        if (contentId == -1 && content == null) {
            response.sendRedirect("content.jsp?activetab=previewcontent");
        } else {
            try {
                TopicMapService topicService = new TopicMapService(request);

                String topicId = param.getString("topicId");
                int topicMapId = param.getInt("topicMapId");

                if (contentId == -1 || (content != null && content.getId() == contentId)) {
                    // Dersom contentId ikke er angitt, brukeren skal fjerne skal siden han redigerer
                    // Dersom angitt og han redigerer på en side fjern også fra den
                    List topics = content.getTopics();
                    if (topics != null) {
                        for (int i = 0; i < topics.size(); i++) {
                            Topic t = (Topic)topics.get(i);
                            if (t.getTopicMapId() == topicMapId && t.getId().equalsIgnoreCase(topicId)) {
                                topics.remove(t);
                                break;
                            }
                        }

                    }
                    content.setIsModified(true);
                }

                if (contentId == -1) {
                    response.sendRedirect("content.jsp?activetab=editmetadata");
                } else {
                    Topic t = new Topic(topicId, topicMapId);
                    topicService.removeTopicContentAssociation(t, contentId);
                    // Fjern fra angitt tema
                    response.sendRedirect("../topicmaps/topic.jsp?topicId=" + topicId + "&topicMapId=" + topicMapId);
                }
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