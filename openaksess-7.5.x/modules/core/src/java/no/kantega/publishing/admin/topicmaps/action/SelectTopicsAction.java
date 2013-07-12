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

package no.kantega.publishing.admin.topicmaps.action;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.common.service.TopicMapService;
import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.topicmaps.data.TopicMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class SelectTopicsAction extends AbstractController {
    public String view;

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request);
        TopicMapService topicMapService = new TopicMapService(request);

        Map<String, Object> model = new HashMap<String, Object>();

        Topic instanceOf = null;
        String mapAndId = param.getString("topictype");
        if (mapAndId != null && mapAndId.length() > 0) {
            String[] topicMapAndId = mapAndId.split(":");
            instanceOf = new Topic();
            instanceOf.setId(topicMapAndId[1]);
            instanceOf.setTopicMapId(Integer.parseInt(topicMapAndId[0]));
        }

        List<TopicMap> topicMaps = topicMapService.getTopicMaps();
        // Get all topic types for each map
        for (TopicMap topicMap : topicMaps) {
            List<Topic> topicTypes = topicMapService.getTopicTypes(topicMap.getId());
            List<Topic> selectableTypes = new ArrayList<Topic>();
            for (Topic topicType : topicTypes) {
                // Only add topic types which are selectable
                if (topicType.isSelectable()) {
                    selectableTypes.add(topicType);
                    if (instanceOf != null && topicType.getTopicMapId() == instanceOf.getTopicMapId() && topicType.getId().equals(instanceOf.getId())) {
                        // Current selected topic type
                        model.put("topics", topicMapService.getTopicsByInstance(instanceOf));
                    }
                }

            }
            topicMap.setTopicTypes(selectableTypes);
        }


        model.put("selectMultiple", param.getBoolean("selectMultiple", false));
        model.put("topicMaps", topicMaps);
        model.put("instanceOf", instanceOf);

        return new ModelAndView(view, model);
    }

    public void setView(String view) {
        this.view = view;
    }
}
