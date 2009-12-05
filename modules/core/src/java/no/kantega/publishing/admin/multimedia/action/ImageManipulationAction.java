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
import no.kantega.commons.log.Log;
import no.kantega.commons.exception.InvalidParameterException;
import no.kantega.publishing.common.util.ImageHelper;
import no.kantega.publishing.common.service.MultimediaService;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.util.InputStreamHandler;
import no.kantega.publishing.admin.viewcontroller.AdminController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.HashMap;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

public class ImageManipulationAction extends AdminController {

    private String view;

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request, "utf-8");

        MultimediaService mediaService = new MultimediaService(request);

        int mmId = param.getInt("id");
        int width  = param.getInt("sizewidth");
        int height = param.getInt("sizeheight");

        if (request.getMethod().equalsIgnoreCase("POST")) {
            try {
                if (mmId == -1 || width == -1 || height == -1) {
                    throw new InvalidParameterException("-", this.getClass().getName());
                }

                int cropx = param.getInt("cropx");
                int cropy = param.getInt("cropy");
                int cropwidth = param.getInt("cropwidth");
                int cropheight = param.getInt("cropheight");

                Multimedia mm = mediaService.getMultimedia(mmId);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                mediaService.streamMultimediaData(mmId, new InputStreamHandler(bos));

                mm.setData(bos.toByteArray());

                if (mm.getMimeType().getType().indexOf("image") != -1) {
                    mm = ImageHelper.resizeAndCropImage(mm, width, height, cropx, cropy, cropwidth, cropheight);

                    if (!param.getBoolean("overwrite")) {
                        // Name new image
                        String name = mm.getName();
                        String suffix = " (" + mm.getWidth() + " x " + mm.getHeight() + ")";
                        if (name.length() + suffix.length() > 255) {
                            name = name.substring(0, 250 - suffix.length()) + "...";
                        }
                        mm.setName(name + suffix);
                        mm.setId(-1);
                    }

                    // Add file ending (jpg/png)
                    String filename = mm.getFilename();
                    if (filename.indexOf(".") != -1) {
                        filename = filename.substring(0, filename.lastIndexOf(".") + 1) + mm.getMimeType().getFileExtension();
                    }
                    mm.setFilename(filename);

                    mmId = mediaService.setMultimedia(mm);
                    mm.setId(mmId);
                }
            } catch (Exception e) {
                Log.error(this.getClass().getName(), e, null, null);
            }
            Map<String, Integer> model = new HashMap<String, Integer>();
            model.put("id", mmId);

            return new ModelAndView(new RedirectView("EditMultimedia.action"), model);

        } else {

            Map<String, Multimedia> model = new HashMap<String, Multimedia>();
            Multimedia mm = mediaService.getMultimedia(mmId);
            model.put("media", mm);

            return new ModelAndView(view, model);
        }
    }

    public void setView(String view) {
        this.view = view;
    }
}

