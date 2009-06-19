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
import no.kantega.commons.exception.InvalidParameterException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.service.MultimediaService;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.MultimediaImageMap;
import no.kantega.publishing.common.util.InputStreamHandler;
import no.kantega.publishing.common.ao.MultimediaImageMapAO;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.*;

public class ImageMapAction  extends HttpServlet {
    private static String SOURCE = "ImageMapAction";

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestParameters param = new RequestParameters(request, "utf-8");
        try {
            int mmId = param.getInt("id");
            if (mmId == -1) {
                throw new InvalidParameterException("-", ImageMapAction.SOURCE);
            }

            MultimediaImageMap mim = new MultimediaImageMap();

            Enumeration e = request.getParameterNames();

            while(e.hasMoreElements()){
                String oneParam = (String)e.nextElement();
                if (oneParam.startsWith("coords")){
                    String linkId = oneParam.substring(6); //alt etter 'coords'
                    String link = param.getString("link"+linkId);
                    String altName = param.getString("altname"+linkId);
                    String nw = param.getString("nyttvindu"+linkId);
                    boolean newWindow = false;
                    if (nw != null && nw.equals("on")) {
                        newWindow = true;
                    }
                    if (link != null) {
                        link = link.trim();
                    }
                    //gjør det enkelt foreløpig:
                    if (link != null && link.length() > 0 && !link.equals("http://")){
                        link = param.getString("link" + linkId);
                        String coords = param.getString("coords" + linkId);
                        mim.addCoordUrlMap(coords, link, altName, newWindow ? 1 : 0);
                    }
                }
            }

            if (!param.getBoolean("overwrite", true)) {
                // Gi nytt navn på bildet
                MultimediaService mediaService = new MultimediaService(request);
                Multimedia mm = mediaService.getMultimedia(mmId);
                String name = mm.getName();
                String suffix = " (bildekart)";
                if (name.length() + suffix.length() > 255) {
                    name = name.substring(0, 250 - suffix.length()) + "...";
                }
                mm.setName(name + suffix);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                mediaService.streamMultimediaData(mmId, new InputStreamHandler(bos));
                mm.setData(bos.toByteArray());

                mm.setId(-1);
                mmId = mediaService.setMultimedia(mm);
            }

            mim.setMultimediaId(mmId);
            MultimediaImageMapAO.storeImageMap(mim);

            response.sendRedirect("multimedia.jsp?activetab=viewmultimedia&id=" + mmId);
        } catch (Exception e) {
            Log.error(ImageMapAction.SOURCE, e, null, null);
        }
    }
}
