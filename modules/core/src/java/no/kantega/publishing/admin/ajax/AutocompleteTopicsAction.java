/*
 * Copyright 2009 Kantega AS
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package no.kantega.publishing.admin.ajax;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.common.service.TopicMapService;
import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.topicmaps.data.TopicMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutocompleteTopicsAction implements Controller {
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        RequestParameters param = new RequestParameters(request);

        int topicMapId = param.getInt("topicMapId");

        String term = param.getString("term");
        if (term != null && term.trim().length() > 0) {
            TopicMapService tms = new TopicMapService(request);
            List<Topic> topics = tms.getTopicsByNameAndTopicMapId(term, topicMapId);
            List<TopicMap> topicMaps = tms.getTopicMaps();
            if (topicMaps.size() > 1) {
                for (Topic topic : topics) {
                    for (TopicMap topicMap : topicMaps) {
                        if (topicMap.getId() == topic.getTopicMapId()) {
                            topic.setBaseName(topic.getBaseName() + " (" + tms.getTopicMap(topic.getTopicMapId()).getName() + ")");
                            break;
                        }
                    }
                }
            }
            model.put("topics", topics);
        }
        return new ModelAndView("/WEB-INF/jsp/ajax/searchresult-topics.jsp", model);
    }
}

