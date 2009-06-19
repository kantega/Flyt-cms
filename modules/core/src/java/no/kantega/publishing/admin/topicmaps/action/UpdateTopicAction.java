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

package no.kantega.publishing.admin.topicmaps.action;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.publishing.common.exception.ExceptionHandler;
import no.kantega.commons.log.Log;
import no.kantega.commons.util.RegExp;
import no.kantega.commons.util.LocaleLabels;
import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.topicmaps.data.TopicOccurence;
import no.kantega.publishing.common.service.TopicMapService;
import no.kantega.publishing.common.Aksess;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class UpdateTopicAction extends HttpServlet {
    private static String SOURCE = "aksess.UpdateTopicAction";
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        RequestParameters param = new RequestParameters(request, "utf-8");
        String task = param.getString("task");

        HttpSession session = request.getSession(true);

        Topic topic   = new Topic();
        Topic current = (Topic)session.getAttribute("currentTopic");
        if (current == null) {
            response.sendRedirect("topicmap.jsp");
        }

        Topic associatiedTopic = null;

        if ("add".equalsIgnoreCase(task)) {
            if (current != null) {
                // Legg til nytt emne
                if (current.isTopicType()) {
                    // Opprett nytt topic av angitt type
                    topic.setInstanceOf(current);
                } else {
                    associatiedTopic = current;
                }
            }
        } else {
            // Endre eksisterende
            topic = current;
        }

        ValidationErrors errors = new ValidationErrors();

        try {
            TopicMapService topicService = new TopicMapService(request);

            String basename = param.getString("name", 62);
            String instanceOf = param.getString("instanceof");
            topic.setBaseName(basename);
            if (instanceOf != null) {
                topic.setInstanceOf(new Topic(instanceOf, topic.getTopicMapId()));
            }

            if (topic.getId() == null) {
                // Ny
                topic.setTopicMapId(current.getTopicMapId());
                String id = basename.toLowerCase();
                id = id.replace('æ', 'e');
                id = id.replace('ø', 'o');
                id = id.replace('å', 'a');
                id = RegExp.replace("[^a-z0-9]", id, "");
                topic.setId(id);

                Topic tmp = topicService.getTopic(topic.getTopicMapId(), id);
                if (tmp != null) {
                    // Feil på siden, send bruker tilbake for å rette opp feil
                    errors.add(null, LocaleLabels.getLabel("aksess.feil.emneibruk", Aksess.getDefaultAdminLocale()));
                }
            }

            List occurences = topic.getOccurences();
            if (occurences != null && occurences.size() > 0) {
                for (int i = 0; i < occurences.size(); i++) {
                    TopicOccurence occurence = (TopicOccurence)occurences.get(i);
                    String resdata = param.getString("occurence_resourcedata_" + i, 6000);
                    if (resdata != null) {
                        occurence.setResourceData(resdata);
                    }
                }
            }

            if (errors.getLength() > 0) {
                session.setAttribute("errors", errors);
                response.sendRedirect("topicmap.jsp?activetab=edittopic&task=" + task);
            } else {
                // Lagre topic
                topicService.setTopic(topic);

                // Lagre tilknyttet topic
                if (associatiedTopic != null) {
                    topicService.addTopicAssociation(topic, associatiedTopic);
                }
                session.setAttribute("currentTopic", topic);
                response.sendRedirect("topicmap.jsp?activetab=topic");
            }

        } catch (Exception e) {
            ExceptionHandler handler = new ExceptionHandler();
            handler.setThrowable(e, SOURCE);
            Log.error(SOURCE, e, null, null);
            request.getSession(true).setAttribute("handler", handler);
            request.getRequestDispatcher(Aksess.ERROR_URL).forward(request, response);
        }
    }

}
