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

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.enums.MultimediaType;
import no.kantega.publishing.common.service.MultimediaService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import java.util.ArrayList;
import java.util.List;

public class PhotoAlbumHelper {
    private static final Logger log = LoggerFactory.getLogger(PhotoAlbumHelper.class);

    public static List<Multimedia> getPhotos(PageContext pageContext, int albumId) {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

        String photoalbum = (String) request.getAttribute("photoalbum");
        if (albumId == -1 && !StringUtils.isBlank(photoalbum)){
            try {
                albumId = Integer.parseInt(photoalbum);
            } catch (NumberFormatException e){
                e.printStackTrace();
                return null;
            }
        }

        if (albumId == -1) return null;

        List<Multimedia> photos = (List<Multimedia>)request.getAttribute("aksess_photos_" + albumId);
        if (photos == null) {
            photos = new ArrayList<>();

            try {
                MultimediaService mediaService = new MultimediaService(request);
                List<Multimedia> tmp = mediaService.getMultimediaList(albumId);
                for (Multimedia multimedia : tmp) {
                    if (multimedia.getType() == MultimediaType.MEDIA) {
                        photos.add(multimedia);
                    }
                }
                request.setAttribute("aksess_photos_" + albumId, photos);
            } catch (SystemException e) {
                log.error("", e);
            }
        }
        return photos;
    }
}
