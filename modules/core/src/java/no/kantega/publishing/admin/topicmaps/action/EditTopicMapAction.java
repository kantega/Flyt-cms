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
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.admin.viewcontroller.AdminController;
import no.kantega.publishing.common.service.TopicMapService;
import no.kantega.publishing.topicmaps.data.TopicMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


/**
 */
public class EditTopicMapAction extends AdminController {
    private static String SOURCE = "aksess.EditTopicMapAction";
    private String view;

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        RequestParameters param = new RequestParameters(request, "utf-8");

        Map<String, Object> model = new HashMap<String, Object>();

        TopicMap topicMap = null;

        int id = param.getInt("id");
        TopicMapService topicService = new TopicMapService(request);

        if (id != -1) {
            topicMap = topicService.getTopicMap(id);
            if (topicMap == null) {
                throw new SystemException("Emnekart med id " + id  + " finnes ikke", SOURCE, null);
            }
        } else {
            topicMap = new TopicMap();
        }

        if (request.getMethod().equalsIgnoreCase("post")) {

            topicMap.setName(param.getString("name", 40));
            topicMap.setUrl(param.getString("url", 255));
            topicMap.setEditable(param.getBoolean("iseditable"));

            topicMap = topicService.setTopicMap(topicMap);

            return new ModelAndView(new RedirectView("ListTopicMaps.action"));
        } else {
            model.put("topicMap", topicMap);
            return new ModelAndView(view, model);
        }

    }

    public void setView(String view) {
        this.view = view;
    }
}
