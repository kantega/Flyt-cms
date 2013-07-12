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
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.service.MultimediaService;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.enums.Privilege;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Edit metadata for a multimedia object
 */
public class EditMultimediaAction extends AbstractEditMultimediaAction {
    private String view;
    private String selectMediaView;

    protected ModelAndView handleGet(Multimedia mm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Configuration c = Aksess.getConfiguration();

        // Show image / media object
        Map<String, Object> model = new HashMap<String, Object>();

        model.put("altNameRequired", c.getBoolean("multimedia.altname.required", false));
        model.put("descriptionRequired", c.getBoolean("multimedia.description.required", false));
        model.put("media", mm);

        if (mm.getMimeType().userMustInputDimension()) {
            model.put("showDimension", Boolean.TRUE);
            if (mm.getWidth() <= 0  || mm.getHeight() <= 0) {
                model.put("showDimensionInfo", Boolean.TRUE);
            }
        }

        boolean canEdit = SecuritySession.getInstance(request).isAuthorized(mm, Privilege.UPDATE_CONTENT);
        if (canEdit) {
            model.put("isPropertyPaneEditable", Boolean.TRUE);
        }

        return new ModelAndView(view, model);
    }

    protected ModelAndView handlePost(Multimedia mm, HttpServletRequest request, HttpServletResponse response) {
        RequestParameters param = new RequestParameters(request, "utf-8");

        boolean changed = param.getBoolean("changed", false);
        boolean insert = param.getBoolean("insert", false);

        boolean canEdit = SecuritySession.getInstance(request).isAuthorized(mm, Privilege.UPDATE_CONTENT);

        MultimediaService mediaService = new MultimediaService(request);

        // Save changes to object
        if (changed && canEdit) {
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

            mm.setId(mediaService.setMultimedia(mm));
        }
        Map<String, Object> model = new HashMap<String, Object>();
        if (insert) {
            model.put("media", mm);
            model.put("maxWidth", param.getInt("maxWidth"));
            return new ModelAndView(selectMediaView, model);
        } else if(param.getInts("ids") != null){
            List<Integer> ids = new ArrayList<Integer>();
            for(int i = 0; i < param.getInts("ids").length; i++){
                ids.add(param.getInts("ids")[i]);
            }
            int id = ids.remove(0);
            model.put("id", mm.getParentId());
            model.put("ids", ids);
            return new ModelAndView(new RedirectView("EditMultimedia.action?id="+id), model);
        } else {
            model.put("id", mm.getParentId());
            return new ModelAndView(new RedirectView("Navigate.action"), model);
        }

    }

    public void setView(String view) {
        this.view = view;
    }

    public void setSelectMediaView(String selectMediaView) {
        this.selectMediaView = selectMediaView;
    }
}

