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
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.service.MultimediaService;
import no.kantega.publishing.common.util.InputStreamHandler;
import no.kantega.publishing.multimedia.ImageEditor;
import no.kantega.publishing.security.SecuritySession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Crop image
 */
public class ImageCropAction extends AbstractEditMultimediaAction {
    private static final Logger log = LoggerFactory.getLogger(ImageCropAction.class);
    private String view;
    private ImageEditor imageEditor;

    protected ModelAndView handleGet(Multimedia mm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<>();
        SecuritySession securitySession = SecuritySession.getInstance(request);
        if (securitySession.isUserInRole(Aksess.getAdminRole()) || securitySession.getUser().getId().equalsIgnoreCase(mm.getModifiedBy())) {
            model.put("allowOverwrite", Boolean.TRUE);
        }

        return new ModelAndView(view, model);
    }

    protected ModelAndView handlePost(Multimedia mm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            RequestParameters param = new RequestParameters(request, "utf-8");

            int cropx = param.getInt("cropx");
            int cropy = param.getInt("cropy");
            int cropwidth = param.getInt("cropwidth");
            int cropheight = param.getInt("cropheight");

            MultimediaService mediaService = new MultimediaService(request);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            mediaService.streamMultimediaData(mm.getId(), new InputStreamHandler(bos));

            mm.setData(bos.toByteArray());

            if (mm.getMimeType().getType().indexOf("image") != -1 && cropwidth > 0 && cropheight > 0) {
                mm = imageEditor.resizeAndCropMultimedia(mm, mm.getWidth(), mm.getHeight(), cropx, cropy, cropwidth, cropheight);
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
                mm.setId(mediaService.setMultimedia(mm));
            }
        } catch (Exception e) {
            log.error("", e);
        }

        Map<String, Integer> model = new HashMap<String, Integer>();
        model.put("id", mm.getId());

        return new ModelAndView(new RedirectView("Navigate.action"), model);
    }

    public void setView(String view) {
        this.view = view;
    }

    public void setImageEditor(ImageEditor imageEditor) {
        this.imageEditor = imageEditor;
    }
}

