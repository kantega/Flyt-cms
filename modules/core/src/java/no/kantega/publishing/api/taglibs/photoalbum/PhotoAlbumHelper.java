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

package no.kantega.publishing.api.taglibs.photoalbum;

import no.kantega.publishing.common.service.MultimediaService;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.enums.MultimediaType;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;

import javax.servlet.jsp.PageContext;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.ArrayList;

public class PhotoAlbumHelper {
    private static final String SOURCE = "aksess.PhotoAlbumHelper";

    public static List getPhotos(PageContext pageContext, int albumId) {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

        if (albumId == -1){
            try {
                albumId = Integer.parseInt((String)request.getAttribute("photoalbum"));
            } catch (NumberFormatException e){
                e.printStackTrace();
                return null;
            }
        }

        if (albumId == -1) return null;

        List photos = (List)request.getAttribute("aksess_photos_" + albumId);
        if (photos == null) {
            photos = new ArrayList();

            try {
                MultimediaService mediaService = new MultimediaService(request);
                List tmp = mediaService.getMultimediaList(albumId);
                for (int i = 0; i < tmp.size(); i++) {
                    Multimedia multimedia = (Multimedia) tmp.get(i);
                    if (multimedia.getType() == MultimediaType.MEDIA) {
                        photos.add(multimedia);
                    }
                }
                request.setAttribute("aksess_photos_" + albumId, photos);
            } catch (SystemException e) {
                Log.error(SOURCE, e, null, null);
            }
        }
        return photos;
    }
}
