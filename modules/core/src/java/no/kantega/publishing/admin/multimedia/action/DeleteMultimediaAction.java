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
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.exception.ObjectInUseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

public class DeleteMultimediaAction implements Controller {
    private String errorView;
    private String beforeDeleteView;
    private String confirmDeleteView;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        RequestParameters param = new RequestParameters(request, "utf-8");
        MultimediaService mediaService = new MultimediaService(request);

        int id = param.getInt("id");

        Map<String, Object> model = new HashMap<String, Object>();

        if (!request.getMethod().equalsIgnoreCase("POST")) {
            // Ask if user wants to delete
            Multimedia mm = mediaService.getMultimedia(id);
            model.put("multimedia", mm);
            return new ModelAndView(beforeDeleteView, model);
        } else {
            int parentId = 0;
            Multimedia mm = mediaService.getMultimedia(id);
            if (mm != null) {
                parentId = mm.getParentId();
                model.put("parentId", parentId);
                try {
                    mediaService.deleteMultimedia(id);

                } catch (ObjectInUseException e) {
                    model.put("error", "feil.no.kantega.publishing.common.exception.ObjectInUseException");
                    return new ModelAndView(errorView, model);
                }

            }
            return new ModelAndView(confirmDeleteView, model);
        }
    }

    public void setErrorView(String errorView) {
        this.errorView = errorView;
    }

    public void setBeforeDeleteView(String beforeDeleteView) {
        this.beforeDeleteView = beforeDeleteView;
    }

    public void setConfirmDeleteView(String confirmDeleteView) {
        this.confirmDeleteView = confirmDeleteView;
    }
}


