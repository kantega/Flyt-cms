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

package no.kantega.publishing.admin.content.htmlfilter;

import no.kantega.commons.xmlfilter.Filter;
import no.kantega.publishing.common.ao.MultimediaDao;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.MultimediaDimensions;
import no.kantega.publishing.common.util.MultimediaHelper;
import no.kantega.publishing.multimedia.ImageEditor;
import no.kantega.publishing.spring.RootContext;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNoneBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class ImgHeightAndWidthFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(ImgHeightAndWidthFilter.class);

    private static MultimediaDao multimediaDao;
    private static ImageEditor imageEditor;

    @Override
    public Document runFilter(Document document) {
        if(multimediaDao == null){
            multimediaDao = RootContext.getInstance().getBean(MultimediaDao.class);
            imageEditor = RootContext.getInstance().getBean(ImageEditor.class);
        }

        for (Element img : document.getElementsByTag("img")) {
            String width = img.attr("width");
            String height = img.attr("height");
            if(isNotBlank(width) && isNoneBlank(height)){
                try {
                    int imageWidth = Integer.parseInt(width);
                    int imageHeight = Integer.parseInt(height);

                    String url = img.attr("src");
                    if (url != null) {
                        List<Integer> ids = MultimediaHelper.getMultimediaIdsFromText(url);
                        if (ids.size() == 1) {
                            int multimediaId = ids.get(0);
                            Multimedia image = multimediaDao.getMultimedia(multimediaId);
                            if (imageWidth != image.getWidth() || imageHeight != image.getHeight()) {
                                MultimediaDimensions d = imageEditor.getResizedImageDimensions(image.getWidth(), image.getHeight(), imageWidth, imageHeight);
                                img.attr("height", String.valueOf(d.getHeight()));
                                img.attr("width", String.valueOf(d.getWidth()));
                                String imageUrl = image.getUrl();
                                img.attr("src", imageUrl
                                        + (imageUrl.contains("?") ? "&" : "?")
                                        + "width=" + d.getWidth());

                            }
                        }
                    }

                } catch (NumberFormatException e) {
                    log.error("Could not parse number", e);
                }
            }
        }
        return document;
    }
}


