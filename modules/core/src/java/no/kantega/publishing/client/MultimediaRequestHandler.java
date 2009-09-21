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

package no.kantega.publishing.client;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.NeedsRefreshException;
import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.log.Log;
import no.kantega.commons.util.HttpHelper;
import no.kantega.commons.configuration.Configuration;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.service.MultimediaService;
import no.kantega.publishing.common.util.InputStreamHandler;
import no.kantega.publishing.common.util.ImageHelper;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MultimediaRequestHandler extends HttpServlet {
    private static String SOURCE = "MultimediaRequestHandler";

    public static Cache thumbnailCache = new Cache(true, true, true, false, null, 1000);

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestParameters param = new RequestParameters(request, "utf-8");

        try {
            MultimediaService mediaService = new MultimediaService(request);

            int mmId = param.getInt("id");
            if(mmId == -1) {
                try {
                    String info = request.getPathInfo();
                    mmId = Integer.parseInt(info.substring(1, info.indexOf(".", 1)));
                } catch (Exception e) {
                }
            }


            if (mmId == -1) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            Multimedia mm = mediaService.getMultimedia(mmId);
            if (mm == null) {
                // Multimedia object not found
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            Log.debug(SOURCE, "Sender mediaobjekt (" + mm.getName() + ", id:" + mm.getId() + ")", null, null);

            String contentDisposition = param.getString("contentdisposition");
            if (!"attachment".equals(contentDisposition)) {
                contentDisposition = "inline";
            }

            String mimetype = mm.getMimeType().getType();
            ServletOutputStream out = response.getOutputStream();

            int width = param.getInt("width");
            int height = param.getInt("height");

            String key = mmId + "-" + width + "-" + height + "-" + mm.getLastModified().getTime();

            if (HttpHelper.isInClientCache(request, response, key, mm.getLastModified())) {
                // Exists in browser cache
                response.setContentType(mimetype);
                response.addHeader("Content-Disposition", contentDisposition + "; filename=\"" + mm.getFilename() + "\"");
                response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }

            // Add cache control headers
            Configuration config = Aksess.getConfiguration();
            int expire = config.getInt("multimedia.expire", -1);
            HttpHelper.addCacheControlHeaders(response, expire);


            if ((mimetype.indexOf("image") != -1) && (width != -1 || height != -1) && (mm.getWidth() != width || (mm.getHeight() != height))) {
                byte[] bytes = null;

                String imageFormat = Aksess.getOutputImageFormat();
                if (width*height <= Aksess.getPngPixelLimit()) {
                    imageFormat = "png";
                }

                try {
                    bytes = (byte[]) thumbnailCache.getFromCache(key);
                } catch (NeedsRefreshException e) {
                    try {
                        Log.debug(SOURCE, "Krymper bilde (" + mm.getName() + ", id:" + mm.getId() + ")", null, null);

                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        mediaService.streamMultimediaData(mmId, new InputStreamHandler(bos));
                        mm.setData(bos.toByteArray());

                        // Shrink image
                        mm = ImageHelper.resizeImage(mm, width, height);
                        bytes = mm.getData();
                        thumbnailCache.putInCache(key, bytes, new String[]{Integer.toString(mmId)});

                        // Hmmm?
                        // thumbnailCache.flushGroup(Integer.toString(mmId));
                    } catch (IOException ie) {
                        // Brukeren har avbrutt nedlasting
                        thumbnailCache.cancelUpdate(key);
                    } catch (Throwable t) {
                        thumbnailCache.cancelUpdate(key);
                        Log.error(SOURCE, t, null, null);
                    }
                }

                // Kan kun generere png eller jpg
                response.setContentType("image/" + imageFormat);
                response.addHeader("Content-Disposition", contentDisposition + "; filename=thumb" + mm.getId() + "." + imageFormat);
                response.addHeader("Content-Length", "" + bytes.length);

                out.write(bytes);

            } else {
                // Send direkte
                response.setContentType(mimetype);
                if (mm.getSize() != 0) {
  	                response.addHeader("Content-Length", "" + mm.getSize());
                }
                response.addHeader("Content-Disposition", contentDisposition + "; filename=" + mm.getFilename());
                mediaService.streamMultimediaData(mmId, new InputStreamHandler(out));
            }

            out.flush();
            out.close();
        } catch (IOException e) {
            // Brukeren har avbrutt
        } catch (Exception e) {
            Log.error(SOURCE, e, null, null);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}