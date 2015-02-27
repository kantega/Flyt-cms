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

package no.kantega.publishing.admin.multimedia.action;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.service.MultimediaService;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.enums.Privilege;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Abstract base class for all actions in multimedia element such as edit, crop and create image map
 */
public abstract class AbstractEditMultimediaAction extends AbstractController {

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request, "utf-8");

        MultimediaService mediaService = new MultimediaService(request);

        int id = param.getInt("id");
        Multimedia mm = mediaService.getMultimedia(id);
        if (mm == null) {
            return new ModelAndView(new RedirectView("Navigate.action"));
        }

        boolean canEdit = SecuritySession.getInstance(request).isAuthorized(mm, Privilege.UPDATE_CONTENT);

        ModelAndView modelAndView;
        if (!request.getMethod().equalsIgnoreCase("POST")) {
            modelAndView = handleGet(mm, request, response);

            Map<String, Object> model = modelAndView.getModel();
            model.put("canEdit", canEdit);
            model.put("media", mm);
            if (mm.getMimeType().getType().contains("image")) {
                // Can crop image
                model.put("isImage", Boolean.TRUE);
            }

            // Find usages of this image/file
            List<Content> usages = mediaService.getUsages(mm.getId());
            model.put("usages", usages);
        } else {
            modelAndView = handlePost(mm, request, response);
        }

        return modelAndView;
    }

    abstract protected ModelAndView handleGet(Multimedia mm, HttpServletRequest request, HttpServletResponse response) throws  Exception;
    abstract protected ModelAndView handlePost(Multimedia mm, HttpServletRequest request, HttpServletResponse response) throws  Exception;
}
