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

package no.kantega.publishing.admin.topicmaps.action;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.client.util.ValidationError;
import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.commons.util.LocaleLabels;
import no.kantega.publishing.admin.topicmaps.action.util.TopicMapHelper;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.service.TopicMapService;
import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.topicmaps.data.TopicOccurence;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class EditTopicAction extends AbstractController {
    private String view;

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        TopicMapService topicMapService = new TopicMapService(request);

        RequestParameters param = new RequestParameters(request);

        Topic topic;
        int topicMapId = param.getInt("topicMapId");
        String topicId = param.getString("topicId");

        if (topicId != null) {
            // Edit of existing topic
            topic = topicMapService.getTopic(topicMapId, topicId);
        } else {
            // Create a new topic
            topic = createNewTopic(topicMapId);

        }

        if (request.getMethod().equalsIgnoreCase("POST")) {
            return handleSubmit(topic, request, response);            
        } else {
            return handleView(topic, request, response);
        }
    }

    public ModelAndView handleView(Topic topic, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request);
        TopicMapService topicMapService = new TopicMapService(request);

        Map<String, Object> model = new HashMap<String, Object>();

        String topicId = param.getString("topicId");
        String associatedTopicId = param.getString("associatedTopicId");

        // Show existing or create topic
        model.put("topic", topic);
        if (associatedTopicId != null) {
            model.put("associatedTopicId", associatedTopicId);
        }
        if (topicId != null) {
            model.put("topicId", topicId);
        }

        model.put("topicTypes", topicMapService.getTopicTypes(topic.getTopicMapId()));

        return new ModelAndView(view, model);
    }


    private ModelAndView handleSubmit(Topic topic, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request);

        String associatedTopicId = param.getString("associatedTopicId");
        String instanceOf = param.getString("instanceOf");

        Map<String, Object> model = new HashMap<String, Object>();

        TopicMapService topicMapService = new TopicMapService(request);

        // Update occurences (data)
        List<TopicOccurence> occurences = topic.getOccurences();
        if (occurences != null && occurences.size() > 0) {
            for (int i = 0; i < occurences.size(); i++) {
                TopicOccurence occurence = occurences.get(i);
                String resdata = param.getString("occurence_resourcedata_" + i, 2000);
                if (resdata != null) {
                    occurence.setResourceData(resdata);
                }
            }
        }

        // Update name
        String name = param.getString("name");
        topic.setBaseName(name);
        if (topic.getId() == null) {
            topic.setId(TopicMapHelper.createTopicIdFromName(name));
            if (topicMapService.getTopic(topic.getTopicMapId(), topic.getId()) != null) {
                addValidationError(request, "aksess.topicmaps.admin.error.topicExists", Pair.of("baseName", topic.getBaseName()));
                // Topic id exists
                return handleView(topic, request, response);
            }
        }

        // Update instance of
        if (instanceOf != null) {
            topic.setInstanceOf(topicMapService.getTopic(topic.getTopicMapId(), instanceOf));
        }

        // Save topic
        topicMapService.setTopic(topic);

        if (associatedTopicId != null) {
            // Add association between topics
            Topic otherTopic = topicMapService.getTopic(topic.getTopicMapId(), associatedTopicId);
            topicMapService.addTopicAssociation(topic, otherTopic);
        }

        model.put("topicMapId", topic.getTopicMapId());
        model.put("topicId", topic.getId());
        
        return new ModelAndView(new RedirectView("ViewTopic.action"), model);
    }

    private Topic createNewTopic(int topicMapId) {
        Topic topic = new Topic();
        topic.setTopicMapId(topicMapId);
        String description = LocaleLabels.getLabel("aksess.topicmaps.description", Aksess.getDefaultAdminLocale());
        TopicOccurence descriptionTopic = new TopicOccurence();
        Topic descriptionInstance = new Topic();
        descriptionInstance.setTopicMapId(topicMapId);
        descriptionInstance.setId(description.toLowerCase());
        descriptionInstance.setBaseName(description);
        descriptionTopic.setInstanceOf(descriptionInstance);
        descriptionTopic.setResourceData("");
        topic.addOccurence(descriptionTopic);
        return topic;
    }

    private void addValidationError(HttpServletRequest request, String errorCode, Pair<String, String> ... parameters) {
        ValidationErrors errors;
        if  (request.getAttribute("errors") != null) {
            errors = (ValidationErrors) request.getAttribute("errors");
        } else {
            errors = new ValidationErrors();
            request.setAttribute("errors", errors);
        }
        Map<String, Object> params = new HashMap<String, Object>();
        if (parameters != null) {
            for (Pair<String, String> p : parameters) {
                params.put(p.getKey(), p.getValue());
            }
        }
        errors.add(new ValidationError(null, errorCode, params));
    }



    public void setView(String view) {
        this.view = view;
    }
}

