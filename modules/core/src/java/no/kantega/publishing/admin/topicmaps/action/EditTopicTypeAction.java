/*
 * Copyright 2011 Kantega AS
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
import no.kantega.publishing.admin.topicmaps.action.util.TopicMapHelper;
import no.kantega.publishing.common.service.TopicMapService;
import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.topicmaps.data.TopicBaseName;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditTopicTypeAction extends AbstractController {
    private String view;
    private TopicMapService topicMapService;

    @Override
    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = null;

        if (request.getMethod().equalsIgnoreCase("post")) {
            saveForm(request);
            int topicMapId = new RequestParameters(request).getInt("topicMapId");
            return new ModelAndView(new RedirectView("ListTopicTypes.action?topicMapId=" + topicMapId));
        }  else {
            model = showForm(request);
        }

        return new ModelAndView(view, model);
    }

    private Map<String, Object> showForm(HttpServletRequest request) {
        topicMapService = new TopicMapService(request);
        RequestParameters params = new RequestParameters(request, "utf-8");
        int topicMapid = params.getInt("topicMapId");
        String topicId = params.getString("topicId");
        Map<String, Object> model = new HashMap<>();

        model.put("topicMapId", topicMapid);
        if (topicId != null) {
            model.put("topic", topicMapService.getTopic(topicMapid, topicId));
        }

        return model;
    }

    private void saveForm(HttpServletRequest request) {
        RequestParameters params = new RequestParameters(request, "utf-8");
        int topicMapId = params.getInt("topicMapId");
        String name = params.getString("name");
        String topicId = params.getString("topicId");

        if (topicId == null) {
            createNewTopic(topicMapId, name);
        } else {
            updateTopic(topicMapId, topicId, name);
        }
    }

    private void createNewTopic(int topicMapId, String name) {
        String id = TopicMapHelper.createTopicIdFromName(name);
        if (topicMapService.getTopic(topicMapId, id) == null) {
            Topic type = new Topic(id, topicMapId);
            type.setIsTopicType(true);
            type.setBaseName(name);
            type.setIsSelectable(true);
            topicMapService.setTopic(type);

            Topic associationType = topicMapService.getTopic(topicMapId, "emne-emne");
            if (associationType == null) {
                createTopicAssociation(topicMapId, id);
            } else {
                if (!scopeFound(id, associationType)) {
                    addScope(id, associationType);
                }
            }
        }
    }

    private void addScope(String scope, Topic associationType) {
        List<TopicBaseName> basenames = associationType.getBaseNames();
        TopicBaseName basename = new TopicBaseName();
        basename.setBaseName("er relatert til");
        basename.setScope(scope);
        basenames.add(basename);
        topicMapService.setTopic(associationType);
    }

    private boolean scopeFound(String scope, Topic associationType) {
        for (TopicBaseName baseName : associationType.getBaseNames()) {
            if (baseName.getScope().equals(scope)) {
                return true;
            }
        }
        return false;
    }

    private void createTopicAssociation(int topicMapId, String scope) {
        Topic associationType = new Topic("emne-emne", topicMapId);
        associationType.setIsAssociation(true);

        List<TopicBaseName> basenames = new ArrayList<TopicBaseName>();
        TopicBaseName basename = new TopicBaseName();
        basename.setBaseName("er relatert til");
        basename.setScope(scope);
        basenames.add(basename);
        associationType.setBaseNames(basenames);

        topicMapService.setTopic(associationType);
    }

    private void updateTopic(int topicMapId, String topicId, String name) {
        Topic topic = topicMapService.getTopic(topicMapId, topicId);
        topic.setBaseName(name);
        topicMapService.setTopic(topic);
    }

    public void setView(String view) {
        this.view = view;
    }
}
