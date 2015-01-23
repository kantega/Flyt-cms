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
import no.kantega.commons.util.HttpHelper;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.ImageResizeParameters;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.service.MultimediaService;
import no.kantega.publishing.common.util.InputStreamHandler;
import no.kantega.publishing.security.SecuritySession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class MultimediaRequestHandler {
    private static final Logger log = LoggerFactory.getLogger(MultimediaRequestHandler.class);

    private MultimediaRequestHandlerHelper helper;
    private int expire;

    private boolean addPagetypeToResponseHeader;

    @PostConstruct
    public void init(){
        Configuration config = Aksess.getConfiguration();
        expire = config.getInt("multimedia.expire", -1);
    }

    @RequestMapping("/multimedia/{id:[0-9]+}/*")
    public void handleMultimedia(@PathVariable int id, HttpServletRequest request, HttpServletResponse response){
        handleRequest(id, request, response);
    }

    @RequestMapping("/multimedia.ap")
    public void handleMultimedia_Ap(@RequestParam int id, HttpServletRequest request, HttpServletResponse response){
        handleRequest(id, request, response);
    }


    private void handleRequest(int id, HttpServletRequest request, HttpServletResponse response) {
        RequestParameters param = new RequestParameters(request, "utf-8");

        try {
            MultimediaService mediaService = new MultimediaService(SecuritySession.getInstance(request));

            Multimedia mm = mediaService.getMultimediaCheckAuthorization(id);
            if (mm == null) {
                log.error("Multimedia with id {} not found", id);
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            log.info( "Sending media object (" + mm.getName() + ", id:" + mm.getId() + ")");

            String contentDisposition = param.getString("contentdisposition");
            if (!"attachment".equals(contentDisposition)) {
                contentDisposition = "inline";
            }

            String mimetype = mm.getMimeType().getType();
            ServletOutputStream out = response.getOutputStream();

            ImageResizeParameters resizeParams = new ImageResizeParameters(param);

            String key = getCacheKey(id, mm, resizeParams);
            if (HttpHelper.isInClientCache(request, response, key, mm.getLastModified())) {
                response.setContentType(mimetype);
                response.addHeader("Content-Disposition", contentDisposition + "; filename=\"" + mm.getFilename() + "\"");
                response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }

            if(addPagetypeToResponseHeader){
                response.addHeader("PageType", "Multimedia");
            }

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
                mediaService.streamMultimediaData(id, new InputStreamHandler(out));
            }

            out.flush();
            out.close();
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private String getCacheKey(int mmId, Multimedia mm, ImageResizeParameters resizeParams) {
        return Integer.toString(mmId) + "-" + resizeParams.toString() + "-" + mm.getLastModified().getTime();
    }

    private boolean shouldResize(String mimetype, ImageResizeParameters resizeParams) {
        return (mimetype.contains("image") && !mimetype.contains("svg")) && !resizeParams.skipResize();
    }

    @Required
    public void setMultimediaRequestHandlerHelper(MultimediaRequestHandlerHelper helper){
        this.helper = helper;
    }

    public void setAddPagetypeToResponseHeader(boolean addPagetypeToResponseHeader) {
        this.addPagetypeToResponseHeader = addPagetypeToResponseHeader;
    }
}
