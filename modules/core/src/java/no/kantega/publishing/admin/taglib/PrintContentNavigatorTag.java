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

import no.kantega.commons.util.LocaleLabels;
import no.kantega.publishing.admin.AdminRequestParameters;
import no.kantega.publishing.admin.util.NavigatorUtil;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.NavigationMapEntry;
import no.kantega.publishing.common.data.SiteMapEntry;
import no.kantega.publishing.common.data.enums.ContentType;

import javax.servlet.jsp.JspWriter;
import java.io.IOException;

public class PrintContentNavigatorTag extends PrintNavigatorTag {
    @Override
    protected void printBody(NavigationMapEntry item) throws IOException {
        SiteMapEntry currentItem = (SiteMapEntry)item;

        JspWriter out = getJspContext().getOut();

        StringBuilder href = new StringBuilder();
        href.append("/content.ap?");
        href.append(AdminRequestParameters.THIS_ID).append("=").append(currentItem.getId()).append("&amp;");
        href.append(AdminRequestParameters.CONTENT_ID).append("=").append(currentItem.getContentId());

        if (currentItem.isHasChildren()) {
            String openState = currentItem.isOpen()? "open": "closed";
            out.write("<span class=\"openState\"><a href=\"" + href + "\" class=\"" + openState + "\"></a></span>");
        } else {
            out.write("<span class=\"openState\"><span class=\"noChildren\"></span></span>");
        }

        ContentType type = currentItem.getType();
        String title = NavigatorUtil.getNavigatorTitle(type, currentItem.getTitle());

        String iconText;
        String iconClass;
        int visibilityStatus = currentItem.getVisibilityStatus();
        if (currentItem.getParentId() == 0) {
            iconClass = "root";
            iconText = title;
        } else {
            iconClass = NavigatorUtil.getIcon(type, visibilityStatus, currentItem.getStatus());
            iconText = NavigatorUtil.getIconText(type, visibilityStatus, currentItem.getStatus());
        }
        out.write("<span class=\"icon\"><a href=\"" + href + "\" class=\""+iconClass+"\" title=\""+iconText+"\"></a></span>");


        boolean isSelected = false;
        // Mark object (shortcuts will not be marked since you go directly to the object)
        if (currentItem.getId() == getCurrentId() && type != ContentType.SHORTCUT) {
            isSelected = true;
        }

        String titleClass = NavigatorUtil.getContextMenuType(type, visibilityStatus, currentItem.getStatus());
        if (isSelected && highlightCurrent) {
            titleClass += " selected";
        }
        out.write("<span class=\"title\"><a href=\""+ href +"\" class=\""+ titleClass +"\" title=\"" + title + "\">" + title +"</a></span>");

        if (currentItem.getNumberOfNotes() > 0) {
            String notesTxt = LocaleLabels.getLabel("aksess.navigator.notes", Aksess.getDefaultAdminLocale());
            out.write("<span class=\"notes\"><a href=\"" + href + "\" title=\"" + + currentItem.getNumberOfNotes() + " " + notesTxt + "\"></a></span>");
        }
    }
}
