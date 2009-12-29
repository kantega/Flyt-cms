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

        String type = "";
        if (currentItem.getType() == MultimediaType.FOLDER) {
            type = "folder";
        } else {
            type = "image";
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
}