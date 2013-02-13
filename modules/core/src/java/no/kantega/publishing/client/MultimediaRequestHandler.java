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

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.log.Log;
import no.kantega.commons.util.HttpHelper;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.ImageResizeParameters;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.service.MultimediaService;
import no.kantega.publishing.common.util.InputStreamHandler;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MultimediaRequestHandler implements Controller {
    private static String SOURCE = "MultimediaRequestHandler";

    private MultimediaRequestHandlerHelper helper;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request, "utf-8");

        try {
            MultimediaService mediaService = new MultimediaService(request);

            int mmId = param.getInt("id");
            if(mmId == -1) {
                try {
                    String info = request.getPathInfo();
                    // URL can be specified in the following formats:
                    // http://localhost/multimedia/1.jpg
                    // http://localhost/multimedia/1/filename.jpg
                    int dotIndex = info.indexOf(".", 1);
                    int slashIndex = info.indexOf("/", 1);
                    if (slashIndex != -1) {
                        mmId = Integer.parseInt(info.substring(1, slashIndex));
                    } else if (dotIndex != -1) {
                        mmId = Integer.parseInt(info.substring(1, dotIndex));
                    }

                } catch (Exception e) {
                    Log.error(SOURCE, e);
                }
            }


            if (mmId == -1) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return null;
            }

            Multimedia mm = mediaService.getMultimediaCheckAuthorization(mmId);
            if (mm == null) {
                // Multimedia object not found
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }

            Log.debug(this.getClass().getSimpleName(), "Sender mediaobjekt (" + mm.getName() + ", id:" + mm.getId() + ")");

            String contentDisposition = param.getString("contentdisposition");
            if (!"attachment".equals(contentDisposition)) {
                contentDisposition = "inline";
            }

            String mimetype = mm.getMimeType().getType();
            ServletOutputStream out = response.getOutputStream();

            ImageResizeParameters resizeParams = new ImageResizeParameters(param);

            String key = getCacheKey(mmId, mm, resizeParams);
            if (HttpHelper.isInClientCache(request, response, key, mm.getLastModified())) {
                response.setContentType(mimetype);
                response.addHeader("Content-Disposition", contentDisposition + "; filename=\"" + mm.getFilename() + "\"");
                response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
                return null;
            }

            // Add cache control headers
            Configuration config = Aksess.getConfiguration();
            int expire = config.getInt("multimedia.expire", -1);
            HttpHelper.addCacheControlHeaders(response, expire);

            if (shouldResize(mimetype, resizeParams)) {
                byte[] bytes = helper.getResizedMultimediaBytes(key, mm, resizeParams, mediaService);

                response.setContentType(mm.getMimeType().getType());
                response.addHeader("Content-Disposition", contentDisposition + "; filename=" + "\"thumb" + mm.getId() + "." + mm.getMimeType().getFileExtension() + "\"");
                response.addHeader("Content-Length", String.valueOf(bytes.length));

                out.write(bytes);

            } else {
                // Send directly
                response.setContentType(mimetype);
                if (mm.getSize() != 0) {
                    response.addHeader("Content-Length", String.valueOf(mm.getSize()));
                }
                response.addHeader("Content-Disposition", contentDisposition + "; filename=\"" + mm.getFilename() + "\"");
                mediaService.streamMultimediaData(mmId, new InputStreamHandler(out));
            }

            out.flush();
            out.close();
        } catch (Exception e) {
            Log.error(SOURCE, e);
        }
        return null;
    }

    private String getCacheKey(int mmId, Multimedia mm, ImageResizeParameters resizeParams) {
        StringBuilder keyBuilder = new StringBuilder(Integer.toString(mmId));
        keyBuilder.append("-");
        keyBuilder.append(resizeParams.toString());
        keyBuilder.append("-");
        keyBuilder.append(mm.getLastModified().getTime());
        return keyBuilder.toString();
    }

    private boolean shouldResize(String mimetype, ImageResizeParameters resizeParams) {
        return (mimetype.contains("image") && !mimetype.contains("svg")) && !resizeParams.skipResize();
    }

    @Required
    public void setMultimediaRequestHandlerHelper(MultimediaRequestHandlerHelper helper){
        this.helper = helper;
    }
}