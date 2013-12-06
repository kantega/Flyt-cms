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
import no.kantega.publishing.common.service.TopicMapService;
import no.kantega.publishing.topicmaps.data.Topic;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * List all topic types for all topic maps
 */
public class SearchTopicsAction extends AbstractController {
    private String view;

    @Override
    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<>();

        RequestParameters param = new RequestParameters(request);
        int topicMapId = param.getInt("topicMapId");

        String q = param.getString("q");
        TopicMapService topicMapService = new TopicMapService(request);

        topicMapService.getTopicsByTopicMapId(topicMapId);

        List<Topic> topics;
        if (q != null && q.length() > 0) {
            topics = topicMapService.getTopicsByNameAndTopicMapId(q, topicMapId);
        } else {
            topics = topicMapService.getTopicsByTopicMapId(topicMapId);
        }

        Map<String, Topic> instanceOfs = new HashMap<String, Topic>();
        for (Topic topic : topics) {
            Topic instanceOf = topic.getInstanceOf();
            Topic t = instanceOfs.get(instanceOf.getId());
            if (t == null) {
                t = topicMapService.getTopic(instanceOf.getTopicMapId(), instanceOf.getId());
                if (t != null) {
                    instanceOfs.put(t.getId(), t);
                } else {
                    t = instanceOf;
                }
            }
            topic.setInstanceOf(t);
        }

        model.put("topicMapId", topicMapId);
        model.put("topics", getAlphabeticalMap(topics));

        return new ModelAndView(view, model);
    }

    public void setView(String view) {
        this.view = view;
    }

    private Map getAlphabeticalMap(List<Topic> allTopics) {
        Map<String, List<Topic>> letters = new TreeMap<String, List<Topic>>();
        for (Topic topic : allTopics) {
            if (topic.getBaseName() != null && topic.getBaseName().trim().length() > 0){
                String letter = topic.getBaseName().substring(0, 1).toUpperCase();
                List<Topic> topicsForLetter = letters.get(letter);
                if (topicsForLetter == null) {
                    topicsForLetter = new ArrayList<Topic>();
                    letters.put(letter, topicsForLetter);
                }
                topicsForLetter.add(topic);
            }
        }

        return letters;
    }

}
