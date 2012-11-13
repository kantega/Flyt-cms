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

package no.kantega.publishing.admin.multimedia.ajax;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.util.LocaleLabels;
import no.kantega.publishing.admin.AdminRequestParameters;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.PathEntry;
import no.kantega.publishing.common.service.MultimediaService;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.enums.Privilege;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class MultimediaPropertiesAction implements Controller {

    private View aksessJsonView;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        RequestParameters param = new RequestParameters(request);
        SecuritySession securitySession = SecuritySession.getInstance(request);

        MultimediaService mediaService = new MultimediaService(request);

        int folderId = param.getInt(AdminRequestParameters.ITEM_IDENTIFIER);
        if (folderId == -1) {
            Multimedia currentMultimedia = (Multimedia)request.getSession(true).getAttribute(AdminSessionAttributes.CURRENT_NAVIGATE_MULTIMEDIA);
            if (currentMultimedia != null) {
                folderId = currentMultimedia.getId();
            } else {
                folderId = 0;
            }
        }

        // Path
        List<PathEntry> path = new ArrayList<PathEntry>();

        // Add root to path
        PathEntry root = new PathEntry(0, LocaleLabels.getLabel("aksess.multimedia.title", Aksess.getDefaultAdminLocale()));
        path.add(root);

        List<String> enabledButtons = new ArrayList<String>();

        if (folderId > 0) {
            Multimedia folder = mediaService.getMultimedia(folderId);
            if (folder != null) {
                model.put("folder", folder);

                // Get path
                path.addAll(mediaService.getMultimediaPath(folder));


                //Add current element to the path.
                PathEntry current = new PathEntry(folder.getId(), folder.getName());
                path.add(current);

                // Check permissions
                boolean canChange = securitySession.isAuthorized(folder, Privilege.APPROVE_CONTENT);
                if (canChange) {
                    enabledButtons.add("UploadButton");
                    enabledButtons.add("NewFolderButton");
                    enabledButtons.add("DeleteFolderButton");
                }
            }
        } else {
            enabledButtons.add("NewFolderButton");
        }

        model.put("path", path);
        model.put("enabledButtons", enabledButtons);

        return new ModelAndView(aksessJsonView, model);
    }

    public void setAksessJsonView(View aksessJsonView) {
        this.aksessJsonView = aksessJsonView;
    }
}
