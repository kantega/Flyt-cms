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

package no.kantega.publishing.admin.content.action;

import no.kantega.publishing.api.content.ContentIdHelper;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.enums.Privilege;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Dialogue which allows user to update display period (publish and expire date) for a page
 */
public class ViewDisplayPeriodAction extends AbstractController {
    private String view;

    @Autowired
    private ContentIdHelper contentIdHelper;

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<>();
        ContentManagementService cms = new ContentManagementService(request);
        String url = request.getParameter("url");
        ContentIdentifier cid = contentIdHelper.fromRequestAndUrl(request, url);
        Content content = cms.getContent(cid, false);

        boolean canUpdateSubpages = false;
        if (SecuritySession.getInstance(request).isAuthorized(content, Privilege.FULL_CONTROL)) {
            canUpdateSubpages = true;
        }
        if (content != null) {
            model.put("content", content);
        }
        model.put("canUpdateSubpages", canUpdateSubpages);
        return new ModelAndView(view, model);
    }

    public void setView(String view) {
        this.view = view;
    }
}
