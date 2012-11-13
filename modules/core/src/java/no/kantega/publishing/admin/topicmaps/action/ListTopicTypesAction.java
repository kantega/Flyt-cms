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
import no.kantega.publishing.common.service.TopicMapService;
import no.kantega.publishing.topicmaps.data.Topic;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListTopicTypesAction extends AbstractController {
    private String view;

    @Override
    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        TopicMapService topicMapService = new TopicMapService(request);
        int topicMapId = new RequestParameters(request).getInt("topicMapId");
        List<Topic> topics = topicMapService.getTopicTypes(topicMapId);

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("topicMapId", topicMapId);
        model.put("topics", topics);
        return new ModelAndView(view, model);
    }

    public void setView(String view) {
        this.view = view;
    }
}
