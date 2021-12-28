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
import no.kantega.publishing.common.data.ContentQuery;
import no.kantega.publishing.common.data.SortOrder;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.service.TopicMapService;
import no.kantega.publishing.topicmaps.data.Topic;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ViewTopicAction extends AbstractController {
    private String view;

    @Override
    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request);

        Map<String, Object> model = new HashMap<>();

        int topicMapId = param.getInt("topicMapId");
        String topicId = param.getString("topicId");

        TopicMapService topicMapService = new TopicMapService(request);
        Topic topic = topicMapService.getTopic(topicMapId, topicId);

        Topic instanceOf = topic.getInstanceOf();
        if (instanceOf != null) {
            model.put("instanceOf", topicMapService.getTopic(topic.getTopicMapId(), instanceOf.getId()));
        }

        ContentManagementService cms = new ContentManagementService(request);

        model.put("topic", topic);

        // Associated content pages
        ContentQuery query = new ContentQuery();
        query.setTopic(topic);
        query.setSortOrder(new SortOrder(ContentProperty.TITLE, false));
        model.put("relatedContent", cms.getContentSummaryList(query));

        // Associated topics
        model.put("associations", topicMapService.getTopicAssociations(topic));

        return new ModelAndView(view, model);
    }

    public void setView(String view) {
        this.view = view;
    }
}
