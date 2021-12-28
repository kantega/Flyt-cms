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

package no.kantega.publishing.admin.topicmaps.ajax;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.service.TopicMapService;
import no.kantega.publishing.topicmaps.data.Topic;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class HandleContentTopicsAction implements Controller {
    private String view;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request);

        boolean add = param.getBoolean("add", false);
        boolean delete = param.getBoolean("remove", false);

        int topicMapId = param.getInt("topicMapId");
        String topicId = param.getString("topicId");

        Map<String, Object> model = new HashMap<>();
        Content content = (Content)request.getSession(true).getAttribute(AdminSessionAttributes.CURRENT_EDIT_CONTENT);
        if (content != null) {
            if (topicMapId > 0 && topicId != null) {
                if (add) {
                    // Add topic to content
                    TopicMapService topicMapService = new TopicMapService(request);
                    Topic topic = topicMapService.getTopic(topicMapId, topicId);
                    if (topic != null) {
                        content.addTopic(topic);
                    }
                } else if (delete) {
                    // Remove topic from content
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
            }
        }

        model.put("content", content);

        return new ModelAndView(view, model);
    }

    public void setView(String view) {
        this.view = view;
    }
}
