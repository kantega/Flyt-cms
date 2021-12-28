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

package no.kantega.publishing.admin.multimedia.action;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.common.service.MultimediaService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class CopyPasteMultimediaAction implements Controller {
    private String view;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        RequestParameters param = new RequestParameters(request, "utf-8");
        Map<String, String> model = new HashMap<String, String>();

        MultimediaService mediaService = new MultimediaService(request);

        int mmId = param.getInt("mmId");
        int newParentId = param.getInt("newParentId");

        mediaService.moveMultimedia(mmId, newParentId);

        model.put("message", "aksess.copypaste.move.multimedia.ok");

        return new ModelAndView(view, model);
    }

    public void setView(String view) {
        this.view = view;
    }
}