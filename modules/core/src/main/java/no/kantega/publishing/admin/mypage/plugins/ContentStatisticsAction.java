/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.admin.mypage.plugins;

import no.kantega.publishing.api.content.ContentAO;
import no.kantega.publishing.api.multimedia.MultimediaAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ContentStatisticsAction implements Controller {

    private String view;

    @Autowired
    private ContentAO contentAO;

    @Autowired
    private MultimediaAO multimediaAO;

    /**
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<>();
        model.put("contentCount", contentAO.getContentCount());
        model.put("linkCount", contentAO.getLinkCount());
        model.put("multimediaCount", multimediaAO.getMultimediaCount());
        model.put("contentProducerCount", contentAO.getContentProducerCount());
        return new ModelAndView(view, model);
    }

    public void setView(String view) {
        this.view = view;
    }

}

