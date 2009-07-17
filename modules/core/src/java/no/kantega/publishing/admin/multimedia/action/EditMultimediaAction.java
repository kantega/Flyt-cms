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
import no.kantega.commons.configuration.Configuration;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.service.MultimediaService;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.admin.viewcontroller.AdminController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class EditMultimediaAction extends AdminController {
    private String view;

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request, "utf-8");

        MultimediaService mediaService = new MultimediaService(request);

        int id = param.getInt("id");
        Multimedia mm = mediaService.getMultimedia(id);
        if (mm == null) {
            return new ModelAndView(new RedirectView("Navigate.action"));
        }

        if (!request.getMethod().equalsIgnoreCase("POST")) {
            // Show image / media object
            Map<String, Object> model = new HashMap<String, Object>();

            Configuration c = Aksess.getConfiguration();

            model.put("altNameRequired", c.getBoolean("multimedia.altname.required", false));
            model.put("descriptionRequired", c.getBoolean("multimedia.description.required", false));
            model.put("media", mm);

            if (mm.getMimeType().userMustInputDimension()) {
                model.put("showDimension", Boolean.TRUE);
                if (mm.getWidth() <= 0  || mm.getHeight() <= 0) {
                    model.put("showDimensionInfo", Boolean.TRUE);
                }
            }

            if (mm.getMimeType().getType().indexOf("image") != -1) {
                // Can crop image
                model.put("showImageCrop", Boolean.TRUE);
            }

            // Find usages of this image/file
            List usages = mediaService.getUsages(mm.getId());
            model.put("usages", usages);


            return new ModelAndView(view, model);
        } else {
            // Save changes to object
            mm.setName(param.getString("name", 255));

            mm.setAltname(param.getString("altname", 255));
            mm.setAuthor(param.getString("author", 255));
            mm.setDescription(param.getString("description", 4000));
            mm.setUsage(param.getString("usage", 4000));

            int width = param.getInt("width");
            int height = param.getInt("height");
            if (width != -1) {
                mm.setWidth(width);
            }
            if (height != -1) {
                mm.setHeight(height);
            }

            mediaService.setMultimedia(mm);

            Map<String, Object> model = new HashMap<String, Object>();
            model.put("id", mm.getParentId());
            return new ModelAndView(new RedirectView("Navigate.action"), model);
        }
    }

    public void setView(String view) {
        this.view = view;
    }
}

