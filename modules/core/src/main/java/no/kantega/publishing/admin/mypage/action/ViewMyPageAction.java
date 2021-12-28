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

package no.kantega.publishing.admin.mypage.action;

import no.kantega.publishing.common.service.ContentManagementService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class ViewMyPageAction extends AbstractController {
    String view;

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<>();

        ContentManagementService cms = new ContentManagementService(request);

        // Get things which should be approved
        model.put("contentForApproval", cms.getContentListForApproval());

        // Get drafts, latest changes etc
        model.put("myWorkList", cms.getMyContentList());

        // Get deleted items
        model.put("myDeletedItems", cms.getDeletedItems());


        return new ModelAndView(view, model);
    }

    public void setView(String view) {
        this.view = view;
    }
}
