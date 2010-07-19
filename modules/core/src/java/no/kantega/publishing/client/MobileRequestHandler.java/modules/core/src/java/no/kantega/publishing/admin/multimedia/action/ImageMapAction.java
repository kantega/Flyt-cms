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
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.MultimediaImageMap;
import no.kantega.publishing.common.ao.MultimediaImageMapAO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;


/**
 * Edit image map
 */
public class ImageMapAction extends AbstractEditMultimediaAction {
    private String view;

    protected ModelAndView handleGet(Multimedia mm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();

        MultimediaImageMap imageMap = MultimediaImageMapAO.loadImageMap(mm.getId());
        if (imageMap != null) {
            model.put("coordinates", imageMap.getCoordUrlMap());
        }

        return new ModelAndView(view, model);
    }

    protected ModelAndView handlePost(Multimedia mm, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request, "utf-8");
        MultimediaImageMap mim = new MultimediaImageMap();

        Enumeration e = request.getParameterNames();

        while(e.hasMoreElements()){
            String oneParam = (String)e.nextElement();
            if (oneParam.startsWith("coords")) {
                String n = oneParam.substring(6); //
                String url = param.getString("url" + n);
                String altName = param.getString("altTitle" + n);
                boolean newWindow = param.getBoolean("newWindow" + n, false);
                if (url != null) {
                    url = url.trim();
                }

                if (url != null && url.length() > 0 && !url.equals("http://")){
                    url = param.getString("url" + n);
                    String coords = param.getString("coords" + n);
                    mim.addCoordUrlMap(coords, url, altName, newWindow ? 1 : 0);
                }
            }
        }

        mim.setMultimediaId(mm.getId());
        MultimediaImageMapAO.storeImageMap(mim);

        Map<String, Integer> model = new HashMap<String, Integer>();
        model.put("id", mm.getId());

        return new ModelAndView(new RedirectView("EditMultimedia.action"), model);
    }

    public void setView(String view) {
        this.view = view;
    }
}
