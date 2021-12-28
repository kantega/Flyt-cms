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
import no.kantega.publishing.common.service.TopicMapService;
import no.kantega.publishing.topicmaps.data.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteTopicAction implements Controller {
    private static final Logger log = LoggerFactory.getLogger(DeleteTopicAction.class);
    
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        TopicMapService topicService = new TopicMapService(request);

        RequestParameters param = new RequestParameters(request);
        int topicMapId = param.getInt("topicMapId");
        String topicId = param.getString("topicId");

        if (topicId != null && topicMapId != -1) {
            Topic topic = topicService.getTopic(topicMapId, topicId);
            if (topic != null) {
                topicService.deleteTopic(topic);
            }            
        }

        return null;
    }
}
