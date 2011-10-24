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
import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.log.Log;
import no.kantega.commons.util.HttpHelper;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.MultimediaDimensions;
import no.kantega.publishing.common.data.enums.Cropping;
import no.kantega.publishing.common.service.MultimediaService;
import no.kantega.publishing.common.util.InputStreamHandler;
import no.kantega.publishing.multimedia.ImageEditor;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MultimediaRequestHandler implements Controller {
    private static String SOURCE = "MultimediaRequestHandler";

    public static Cache thumbnailCache = new Cache(true, true, true, false, null, 1000);

    private ImageEditor imageEditor;

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
                }
            }


            if (mmId == -1) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return null;
            }

            Multimedia mm = mediaService.getMultimedia(mmId);
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

            MultimediaDimensions dimensions = calculateDimensions(param, mm);

            int width = dimensions.getWidth();
            int height = dimensions.getHeight();
            Cropping cropping   = Cropping.valueOf(ServletRequestUtils.getStringParameter(request, "cropping", "contain"));
            if (width == -1 || height == -1) cropping = Cropping.CONTAIN; // default

            String key = mmId + "-" + width + "-" + height + "-" + cropping.getTypeAsString() + "-"  + mm.getLastModified().getTime();

            if (HttpHelper.isInClientCache(request, response, key, mm.getLastModified())) {
                // Exists in browser cache
                response.setContentType(mimetype);
                response.addHeader("Content-Disposition", contentDisposition + "; filename=\"" + mm.getFilename() + "\"");
                response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
                return null;
            }

            // Add cache control headers
            Configuration config = Aksess.getConfiguration();
            int expire = config.getInt("multimedia.expire", -1);
            HttpHelper.addCacheControlHeaders(response, expire);


            if ((mimetype.indexOf("image") != -1) && (width != -1 || height != -1) && (mm.getWidth() != width || (mm.getHeight() != height))) {
                byte[] bytes = null;

                try {
                    bytes = (byte[]) thumbnailCache.getFromCache(key);
                    throw new NeedsRefreshException(bytes);
                } catch (NeedsRefreshException e) {
                    try {
                        Log.debug(SOURCE, "Resizing image (" + mm.getName() + ", id:" + mm.getId() + ")", null, null);

                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        mediaService.streamMultimediaData(mmId, new InputStreamHandler(bos));
                        mm.setData(bos.toByteArray());

                        // shrink
                        mm = imageEditor.resizeMultimedia(mm, width, height, cropping);
                        // crop
                        mm = imageEditor.cropMultimedia(mm, width, height, cropping);

                        bytes = mm.getData();
                        thumbnailCache.putInCache(key, bytes, new String[]{Integer.toString(mmId)});

                    } catch (IOException ie) {
                        // User has cancelled download
                        thumbnailCache.cancelUpdate(key);
                    } catch (Throwable t) {
                        thumbnailCache.cancelUpdate(key);
                        Log.error(SOURCE, t, null, null);
                    }
                }

                response.setContentType(mm.getMimeType().getType());
                response.addHeader("Content-Disposition", contentDisposition + "; filename=thumb" + mm.getId() + "." + mm.getMimeType().getFileExtension());
                response.addHeader("Content-Length", "" + bytes.length);

                out.write(bytes);

            } else {
                // Send directly
                response.setContentType(mimetype);
                if (mm.getSize() != 0) {
                    response.addHeader("Content-Length", "" + mm.getSize());
                }
                response.addHeader("Content-Disposition", contentDisposition + "; filename=" + mm.getFilename());
                mediaService.streamMultimediaData(mmId, new InputStreamHandler(out));
            }

            out.flush();
            out.close();
        } catch (Exception e) {
            Log.error(SOURCE, e, null, null);
        }
        return null;
    }

    /**
     * Calculates dimensions for multimedia resizing based on get-parameters
     * use of minheight and minwidth parameters ensures that the resized image is never smaller than either of the parameters
     * while height and width parameters ensures that the resized image is never bigger than either of the parameters.
     * when combined, min* parameters takes presedence.
     * @param param
     * @param mm
     * @return
     */
    private MultimediaDimensions calculateDimensions(RequestParameters param, Multimedia mm) {

        int     width       = param.getInt("width");
        int     height      = param.getInt("height");
        double  minWidth    = param.getInt("minwidth");
        double  minHeight   = param.getInt("minheight");

        if (minWidth > 0 || minHeight > 0){

            double mmWidth = mm.getWidth();
            double mmHeight = mm.getHeight();

            double newWidth = -1;
            double newHeight = -1;

            if (minWidth > mmWidth) minWidth = mmWidth;
            if (minHeight > mmHeight) minHeight = mmHeight;

            double aspectRatio = (mmWidth / mmHeight);

            if (newWidth < minWidth && minWidth > 0){
                newHeight = (int) Math.ceil(minWidth / aspectRatio);
                newWidth = (int) Math.ceil(( aspectRatio * newHeight));
            }
            if (newHeight < minHeight && minHeight > 0){
                newWidth = (int) Math.ceil(( aspectRatio * minHeight));
                newHeight = (int) Math.ceil(newWidth / aspectRatio);
            }
            height = newHeight > 0 ? (int) newHeight: (int) mmHeight;
            width = newWidth > 0 ? (int) newWidth: (int) mmWidth;
        }

        // clamp - ensure no enlarging
        if (height > mm.getHeight()) height = mm.getHeight();
        if (width > mm.getWidth()) width = mm.getWidth();


        return new MultimediaDimensions(width, height);
    }

    public void setImageEditor(ImageEditor imageEditor) {
        this.imageEditor = imageEditor;
    }
}