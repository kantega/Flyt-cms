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

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.admin.AdminRequestParameters;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.security.SecuritySession;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class ViewUploadMultimediaFormAction implements Controller {

    private String view;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters params = new RequestParameters(request);

        SecuritySession securitySession = SecuritySession.getInstance(request);

        int folderId = params.getInt("parentId");
        int id = params.getInt("id");

        if (folderId == -1) {
            folderId = 0;
        }

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("parentId", folderId);
        model.put("id", id);
        model.put("altNameRequired", Aksess.getConfiguration().getBoolean("multimedia.altname.required", false));
        model.put("allowPreserveImageSize", securitySession.isUserInRole(Aksess.getPhotographerRoles()));

        return new ModelAndView(view, model);
    }

    public void setView(String view) {
        this.view = view;
    }
}
