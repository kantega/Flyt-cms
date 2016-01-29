/*
 * Copyright 2010 Kantega AS
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

package no.kantega.publishing.admin.multimedia.ajax;

import no.kantega.publishing.admin.content.util.AttachmentBlacklistHelper;
import no.kantega.publishing.api.configuration.SystemConfiguration;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.security.SecuritySession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * Displays the multimedia file upload form
 */
@Controller
public class ViewUploadMultimediaFormController {

    private static final String UPLOAD_FORM_VIEW = "/WEB-INF/jsp/admin/multimedia/uploadform.jsp";

    @Autowired
    private SystemConfiguration configuration;

    @RequestMapping(value = "/admin/multimedia/ViewUploadMultimediaForm.action", method = RequestMethod.GET)
    public String showForm(Model model,
                           @RequestParam(required = false, defaultValue = "-1") Integer id,
                           @RequestParam(required = false, defaultValue = "-1") Integer folderId,
                           @RequestParam(required = false, defaultValue = "false") Boolean fileUploadedFromEditor,
                           @RequestParam(defaultValue = "false") Boolean doInsertTag,
                           HttpServletRequest request) throws Exception {

        SecuritySession securitySession = SecuritySession.getInstance(request);

        if (folderId == -1) {
            folderId = 0;
        }

        model.addAttribute("parentId", folderId);
        model.addAttribute("fileUploadedFromEditor", fileUploadedFromEditor);
        model.addAttribute("doInsertTag", doInsertTag);
        model.addAttribute("id", id);
        model.addAttribute("blacklistedFileTypes", AttachmentBlacklistHelper.getBlacklistedFileTypes());
        model.addAttribute("blacklistedErrorMessage", AttachmentBlacklistHelper.getErrorMessage());
        model.addAttribute("mediaNameRequired", configuration.getBoolean("multimedia.medianame.required", false));
        model.addAttribute("altNameRequired", configuration.getBoolean("multimedia.altname.required", false));
        model.addAttribute("authorRequired", configuration.getBoolean("multimedia.author.required", false));
        model.addAttribute("allowPreserveImageSize", !Aksess.isPreserveImageSize() && securitySession.isUserInRole(Aksess.getPhotographerRoles()));

        return UPLOAD_FORM_VIEW;
    }

}
