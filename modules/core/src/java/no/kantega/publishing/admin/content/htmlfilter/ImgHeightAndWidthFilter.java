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

import no.kantega.publishing.admin.content.htmlfilter.util.HtmlFilterHelper;
import no.kantega.publishing.common.ao.MultimediaAO;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.MultimediaDimensions;
import no.kantega.publishing.common.util.MultimediaHelper;
import no.kantega.publishing.multimedia.ImageEditor;
import no.kantega.publishing.spring.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

import java.util.List;

public class ImgHeightAndWidthFilter extends XMLFilterImpl {
    private static final Logger log = LoggerFactory.getLogger(ImgHeightAndWidthFilter.class);
    @Override
    public void startElement(String string, String localName, String name, Attributes attributes) throws SAXException {
        if(localName.equalsIgnoreCase("img")) {
            String width = attributes.getValue("width");
            String height = attributes.getValue("height");

            // Replace image URL with resized image URL if necessary
            if (width != null && height != null) {
                try {
                    int imageWidth = Integer.parseInt(width);
                    int imageHeight = Integer.parseInt(height);

                    String url = attributes.getValue("src");
                    if (url != null) {
                        List<Integer> ids = MultimediaHelper.getMultimediaIdsFromText(url);
                        if (ids.size() == 1) {
                            int multimediaId = ids.get(0);
                            Multimedia image = MultimediaAO.getMultimedia(multimediaId);
                            if (imageWidth != image.getWidth() || imageHeight != image.getHeight() ) {
                                ImageEditor imageEditor = (ImageEditor) RootContext.getInstance().getBean("aksessImageEditor");

                                MultimediaDimensions d = imageEditor.getResizedImageDimensions(image.getWidth(), image.getHeight(), imageWidth, imageHeight);
                                attributes = HtmlFilterHelper.setAttribute("height", "" + d.getHeight(), attributes);
                                attributes = HtmlFilterHelper.setAttribute("width", "" + d.getWidth(), attributes);
                                String resizedImageUrl = image.getUrl();
                                resizedImageUrl += resizedImageUrl.indexOf("?") == -1 ? "?" : "&";
                                resizedImageUrl += "width=" + d.getWidth();
                                attributes = HtmlFilterHelper.setAttribute("src", resizedImageUrl, attributes);
                            }                           
                        }
                    }

                } catch (NumberFormatException e) {
                    log.error("Could not parse number", e);
                }
            }

        }
        super.startElement(string, localName, name, attributes);
    }

    @Override
    public void endElement(String string, String localname, String name) throws SAXException {
        super.endElement(string, localname, name);
    }
}


