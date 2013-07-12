/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package no.kantega.publishing.admin.taglib;

import no.kantega.publishing.admin.AdminRequestParameters;
import no.kantega.publishing.common.data.MultimediaMapEntry;
import no.kantega.publishing.common.data.NavigationMapEntry;
import no.kantega.publishing.common.data.enums.MultimediaType;

import javax.servlet.jsp.JspWriter;
import java.io.IOException;

public class PrintMultimediaNavigatorTag  extends PrintNavigatorTag {
    @Override
    protected void printBody(NavigationMapEntry item) throws IOException {
        MultimediaMapEntry currentItem = (MultimediaMapEntry)item;

        JspWriter out = getJspContext().getOut();

        StringBuilder href = new StringBuilder();
        href.append("?");
        href.append(AdminRequestParameters.ITEM_IDENTIFIER).append("=").append(currentItem.getId());

        if (currentItem.isHasChildren()) {
            String openState = currentItem.isOpen()? "open": "closed";
            out.write("<span class=\"openState\"><a href=\"" + href + "\" class=\"" + openState + "\"></a></span>");
        } else {
            out.write("<span class=\"openState\"><span class=\"noChildren\"></span></span>");
        }

        String type = (currentItem.getDepth() == 0)? "root " : "";
        if (currentItem.getType() == MultimediaType.FOLDER) {
            type += "folder";
        } else {
            type += "media " + getMimeTypeAsClassName(currentItem.getMimeType().getType());
        }
        out.write("<span class=\"icon\"><a href=\"" + href + "\" class=\""+type+"\"></a></span>");


        boolean isSelected = false;
        if (currentItem.getId() == getCurrentId()) {
            isSelected = true;
        }

        String titleClass = type;
        if (isSelected) {
            titleClass += " selected";
        }
        out.write("<span class=\"title\"><a href=\""+ href +"\" class=\""+ titleClass +"\" title=\"" + currentItem.getName() + "\">" + currentItem.getName() +"</a></span>");
    }


    /**
     * Simplifies and generifies mime types. All images will get class name 'image', all videos will get class name 'video' etc.
     * The full mime type is also appended, though with characters undesirable in class names replaced with hyphens, e.g. 'image image-png'
     * @param type mime type, e.g. 'application/pdf'
     * @return 'classified' mime type
     */
    private String getMimeTypeAsClassName(String type) {
        type =  type.replaceAll("(/|\\.|\\+)", "-");
        String simplifiedType = "";
        if (type.startsWith("image")) { simplifiedType = "image"; }
        else if (type.startsWith("video")) { simplifiedType = "video"; }
        else if (type.startsWith("video")) { simplifiedType = "video"; }
        else if (type.startsWith("audio")) { simplifiedType = "audio"; }
        else if (type.contains("excel") || type.contains("spreadsheet")) { simplifiedType = "excel"; }
        else if (type.contains("powerpoint") || type.contains("presentation")) { simplifiedType = "presentation"; }
        else if (type.contains("word") || type.contains("processingml.document") || type.contains("opendocument.text")) { simplifiedType = "word"; }
        return simplifiedType + " " + type;
    }
}