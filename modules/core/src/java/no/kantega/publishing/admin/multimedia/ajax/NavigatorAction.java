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

package no.kantega.publishing.admin.multimedia.ajax;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.util.StringHelper;
import no.kantega.publishing.admin.AdminRequestParameters;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.admin.util.NavigatorUtil;
import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.service.MultimediaService;

public class NavigatorAction implements Controller {

    private String view;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(true);
        MultimediaService mediaService = new MultimediaService(request);
        RequestParameters params = new RequestParameters(request);

        int selectedId = params.getInt(AdminRequestParameters.ITEM_IDENTIFIER);
        boolean expand = params.getBoolean(AdminRequestParameters.EXPAND, true);
        String openFoldersList = params.getString(AdminRequestParameters.NAVIGATION_OPEN_FOLDERS);
        boolean getFoldersOnly = params.getBoolean(AdminRequestParameters.MULTIMEDIA_GET_FOLDERS_ONLY, false);

        if (openFoldersList == null || openFoldersList.length() == 0) {
            openFoldersList = "0";
        }

        String path = null;
        Multimedia currentMultimedia = (Multimedia)session.getAttribute(AdminSessionAttributes.CURRENT_MULTIMEDIA);
        if (currentMultimedia != null) {
            selectedId = currentMultimedia.getId();
            List<PathEntry> pathList = mediaService.getMultimediaPath(currentMultimedia);
            StringBuilder pathBuilder = new StringBuilder();
            for (int i = 0; i < pathList.size(); i++) {
                PathEntry entry = pathList.get(i);
                if (i > 0) {
                    pathBuilder.append(",");
                }
                pathBuilder.append(entry.getId());
            }
            if (pathBuilder.length() > 0) {
                path = pathBuilder.toString();
            }
        }

        openFoldersList = NavigatorUtil.getOpenFolders(expand, openFoldersList, path);
         int[] openIds = StringHelper.getInts(openFoldersList, ",");
        MultimediaMapEntry mediaArchiveRoot = mediaService.getPartialMultimediaMap(openIds, getFoldersOnly);

        Map model = new HashMap();

        model.put(AdminRequestParameters.NAVIGATION_OPEN_FOLDERS, openFoldersList);
        model.put(AdminRequestParameters.ITEM_IDENTIFIER, selectedId);
        model.put(AdminRequestParameters.MULTIMEDIA_GET_FOLDERS_ONLY, getFoldersOnly);
        model.put(AdminRequestParameters.MULTIMEDIA_ARCHIVE_ROOT, mediaArchiveRoot);

        return new ModelAndView(view, model);
    }

    public void setView(String view) {
        this.view = view;
    }
}
