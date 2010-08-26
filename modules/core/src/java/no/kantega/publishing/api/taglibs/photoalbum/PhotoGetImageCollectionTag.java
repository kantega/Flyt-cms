/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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

import javax.servlet.jsp.jstl.core.LoopTagSupport;
import javax.servlet.jsp.JspTagException;
import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author andska, jogri
 */
public class PhotoGetImageCollectionTag extends LoopTagSupport {

    private int folder = -1;
    private boolean shuffle;
    private int shuffleMax = -1;
    private Iterator i;

    protected Object next() throws JspTagException {
        if(i != null) {
            return i.next();
        }
        return null;
    }

    protected boolean hasNext() throws JspTagException {
        if(i == null){
            return false;
        }
        return i.hasNext();
    }

    protected void prepare() throws JspTagException {
        if (folder != -1) {
            MultimediaService mediaService = new MultimediaService((HttpServletRequest)pageContext.getRequest());

            List<Multimedia> multimediaObjects = mediaService.getMultimediaList(folder);
            if(shuffle) {
                Collections.shuffle(multimediaObjects);
                if ((shuffleMax != -1) && (multimediaObjects.size() > shuffleMax)) {
                    multimediaObjects = multimediaObjects.subList(0, shuffleMax);
                }
            }

            List<Multimedia> images = new ArrayList<Multimedia>();
            for (Multimedia multimedia : multimediaObjects) {
                if ((multimedia.getType() == MultimediaType.MEDIA) && multimedia.getMimeType().getType().contains("image")) {
                    images.add(multimedia);
                }
            }
            i = images.iterator();
        }

        folder = -1;
    }

    public void setFolder(int folder) {
        this.folder = folder;
    }

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
    }

    public void setShuffleMax(int shuffleMax) {
        this.shuffleMax = shuffleMax;
    }
}
