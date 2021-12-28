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

package no.kantega.publishing.admin.security.action;

import no.kantega.commons.client.util.RequestParameters;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * User: Anders Skar, Kantega AS
 * Date: Jan 19, 2009
 * Time: 4:22:59 PM
 */
public class RemovePermissionAction extends AbstractController {
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(true);
        List permissions = (List)session.getAttribute(EditPermissionsAction.PERMISSIONS_LIST);
        if (permissions != null) {
            RequestParameters param = new RequestParameters(request);
            int removeId = param.getInt("removeId");
            if (removeId != -1) {
                permissions.remove(removeId);
            }
        }

        return new ModelAndView(new RedirectView("EditPermissions.action"));
    }
}
