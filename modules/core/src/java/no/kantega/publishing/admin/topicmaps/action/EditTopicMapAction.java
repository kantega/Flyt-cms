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

import no.kantega.publishing.common.service.TopicMapService;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.topicmaps.data.TopicMap;
import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.topicmaps.data.TopicBaseName;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.exception.ExceptionHandler;
import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.log.Log;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;


/**
 */
public class EditTopicMapAction extends AbstractController {
    private static String SOURCE = "aksess.EditTopicMapAction";

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        RequestParameters param = new RequestParameters(request, "utf-8");

        Map<String, Object> model = new HashMap<String, Object>();

        TopicMap topicMap = null;

        int id = param.getInt("id");
        TopicMapService topicService = new TopicMapService(request);

        if (id != -1) {
            topicMap = topicService.getTopicMap(id);
            if (topicMap == null) {
                throw new SystemException("Emnekart med id " + id  + " finnes ikke", SOURCE, null);
            }
        } else {
            topicMap = new TopicMap();
        }

        if (request.getMethod().equalsIgnoreCase("post")) {

            topicMap.setName(param.getString("name", 40));
            topicMap.setEditable(param.getBoolean("iseditable"));
            topicMap.setWSOperation(param.getString("wsoperation", 64));
            topicMap.setWSSoapAction(param.getString("wssoapaction", 255));
            topicMap.setWSEndPoint(param.getString("wsendpoint", 255));

            topicMap = topicService.setTopicMap(topicMap);

            if (topicMap.isEditable()) {
                // Legger til basistype emne og kopling mellom emner, inntil dette evt blir en funksjon i GUI for å lage typer topics og koplinger
                Topic type = topicService.getTopic(topicMap.getId(), "emne");
                if (type == null) {
                    // Legg til emne som type topic
                    type = new Topic("emne", topicMap.getId());
                    type.setIsTopicType(true);
                    type.setBaseName("Emne");
                    topicService.setTopic(type);

                    Topic associationType = new Topic("emne-emne", topicMap.getId());
                    associationType.setIsAssociation(true);

                    List basenames = new ArrayList();
                    TopicBaseName basename = new TopicBaseName();
                    basename.setBaseName("er relatert til");
                    basename.setScope("emne");
                    basenames.add(basename);
                    associationType.setBaseNames(basenames);

                    topicService.setTopic(associationType);
                }
            }

            return new ModelAndView(new RedirectView("ListTopicMaps.action"));
        } else {
            model.put("topicMap", topicMap);
            return new ModelAndView("/WEB-INF/jsp/admin/topicmaps/admin/edittopicmap.jsp", model);
        }

    }
}
