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

package no.kantega.publishing.admin.multimedia.action;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.admin.model.Clipboard;
import no.kantega.publishing.api.path.PathEntry;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.service.MultimediaService;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.enums.Privilege;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Anders Skar, Kantega AS
 * Date: Oct 2, 2007
 * Time: 10:17:34 AM
 */
public class ConfirmCopyPasteMultimediaAction implements Controller {
    private String errorView;
    private String view;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map model = new HashMap();

        RequestParameters param = new RequestParameters(request, "utf-8");

        MultimediaService mediaService = new MultimediaService(request);

        SecuritySession securitySession = SecuritySession.getInstance(request);

        int newParentId = param.getInt("newParentId");
        Clipboard clipboard = (Clipboard)request.getSession(true).getAttribute(AdminSessionAttributes.CLIPBOARD_MEDIA);
        if (clipboard == null || clipboard.getItems() == null || clipboard.getItems().size() == 0) {
            model.put("error", "aksess.copypaste.emptyclipboard");
            return new ModelAndView(errorView, model);
        }

        Multimedia multimedia = (Multimedia)clipboard.getItems().get(0);
        Multimedia newParent = null;
        if (newParentId > 0) {
            newParent = mediaService.getMultimedia(newParentId);
        } else {
            newParent = new Multimedia();
            newParent.setName("Rot-katalog");
            newParent.setId(0);
            newParent.setSecurityId(0);
        }

        boolean isAuthorized = false;
        if (securitySession.isAuthorized(newParent, Privilege.UPDATE_CONTENT)) {
            if (securitySession.isAuthorized(multimedia, Privilege.UPDATE_CONTENT)) {
                isAuthorized = true;
            }
        }

        boolean recursive = false;
        List parents = mediaService.getMultimediaPath(newParent);
        if (parents != null && parents.size() > 0) {
            for (int i = 0; i < parents.size(); i++) {
                PathEntry parent = (PathEntry)parents.get(i);
                if (parent.getId() == multimedia.getId()) {
                    recursive = true;
                    break;
                }
            }
        }

        String error = null;

        if (!isAuthorized) {
            // Not authorized
            error = "aksess.copypaste.notauthorized";
        } else if (recursive) {
            // Recursive copy
            error = "aksess.copypaste.recursion";
        }

        if (error != null) {
            model.put("error", error);
            return new ModelAndView(errorView, model);
        } else {
            model.put("multimedia", multimedia);
            model.put("newParent", newParent);
            return new ModelAndView(view, model);
        }
    }

    public void setErrorView(String errorView) {
        this.errorView = errorView;
    }

    public void setView(String view) {
        this.view = view;
    }
}