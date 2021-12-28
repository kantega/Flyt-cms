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

package no.kantega.publishing.admin.content.action;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.exception.InvalidParameterException;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentCreateParameters;
import no.kantega.publishing.common.service.ContentManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class SelectTemplateAction implements Controller {
    private static final Logger log = LoggerFactory.getLogger(SelectTemplateAction.class);

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ContentManagementService cms = new ContentManagementService(request);

        RequestParameters param = new RequestParameters(request, "utf-8");
        String templateId = param.getString("templateId");
        int mainParentId = param.getInt("mainParentId");
        int[] parentIds = param.getInts("parentIds");
        int category = param.getInt("associationCategory");

        if (templateId == null || templateId.length() == 0 || mainParentId == -1) {
            log.error("Missing templateId and mainParentId");
            throw new InvalidParameterException("templateId == -1 || mainParentId == -1");
        }

        int displayTemplateId = -1;
        int contentTemplateId = -1;

        // Links and files dont have displaytemplate, only contenttemplate
        String type = templateId.substring(0, templateId.indexOf("_"));
        templateId = templateId.substring(templateId.indexOf("_") + 1, templateId.length());
        if (type.equalsIgnoreCase("ct")) {
            contentTemplateId = Integer.parseInt(templateId);
        } else {
            displayTemplateId = Integer.parseInt(templateId);
        }

        ContentCreateParameters createParam = new ContentCreateParameters();
        createParam.setCategoryId(category);
        createParam.setDisplayTemplateId(displayTemplateId);
        createParam.setContentTemplateId(contentTemplateId);
        createParam.setMainParentId(mainParentId);
        createParam.setParentIds(parentIds);

        Content content = cms.createNewContent(createParam);

        HttpSession session = request.getSession();
        session.setAttribute(AdminSessionAttributes.CURRENT_EDIT_CONTENT, content);

        return new ModelAndView(new RedirectView("SaveContent.action"));
    }

}