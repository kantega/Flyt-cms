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

import no.kantega.publishing.admin.viewcontroller.AdminController;
import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.common.service.TopicMapService;
import no.kantega.commons.client.util.RequestParameters;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
public abstract class AbstractTopicInfoAction extends AdminController {
    public abstract ModelAndView handleTopicInfoRequest(HttpServletRequest request, HttpServletResponse response, Topic topic);

    protected String view;

    @Override
    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request);

        String topicId = param.getString("topicId");
        int topicMapId = param.getInt("topicMapId");


        TopicMapService topicMapService = new TopicMapService(request);

        return handleTopicInfoRequest(request, response, topicMapService.getTopic(topicMapId, topicId));
    }

    public void setView(String view) {
        this.view = view;
    }
}
