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
import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.topicmaps.data.TopicMap;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ListAssociatedTopicsAction extends AbstractTopicInfoAction {
    @Override
    public ModelAndView handleTopicInfoRequest(HttpServletRequest request, HttpServletResponse response, Topic topic) {
        Map<String, Object> model = new HashMap<>();

        model.put("topic", topic);
        
        TopicMapService topicService = new TopicMapService(request);

        RequestParameters param = new RequestParameters(request);
        String deleteTopicId = param.getString("deleteId");
        if (deleteTopicId != null) {
            // Delete association between topics
            Topic topic2 = new Topic();
            topic2.setId(deleteTopicId);
            topic2.setTopicMapId(topic.getTopicMapId());

            topicService.removeTopicAssociation(topic, topic2);
        }

        String addTopicId = param.getString("addId");
        if (addTopicId != null) {
            // Add association between topics
            Topic topic2 = new Topic();
            topic2.setId(addTopicId);
            topic2.setTopicMapId(topic.getTopicMapId());
            topicService.addTopicAssociation(topic, topic2);
        }

        SecuritySession session = SecuritySession.getInstance(request);
        if (topic != null && session.isUserInRole(Aksess.getAdminRole())) {
            TopicMap tm = topicService.getTopicMap(topic.getTopicMapId());
            if (tm.isEditable()) {
                model.put("canAdd", Boolean.TRUE);
                model.put("canDelete", Boolean.TRUE);
            }
        }

        model.put("associatedTopics", topicService.getTopicAssociations(topic));

        return new ModelAndView(view, model);
    }
}
