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

package no.kantega.publishing.admin.templateconfiguration.action;

import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import no.kantega.publishing.common.data.enums.AttributeDataType;
import no.kantega.publishing.common.cache.TemplateConfigurationCache;
import no.kantega.commons.client.util.RequestParameters;

/**
 * User: Anders Skar, Kantega AS
 * Date: Feb 4, 2009
 * Time: 10:13:54 AM
 */
public class ListContentTemplatesAction extends AbstractController {
    private TemplateConfigurationCache templateConfigurationCache;
    
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();

        RequestParameters param = new RequestParameters(request);
        int type = AttributeDataType.CONTENT_DATA;
        if (param.getInt("type") != -1) {
            type = param.getInt("type");
        }

        boolean isContentTemplates = true;

        List templates;
        if (type == AttributeDataType.CONTENT_DATA) {
            templates = templateConfigurationCache.getTemplateConfiguration().getContentTemplates();
        } else {
            isContentTemplates = false;
            templates = templateConfigurationCache.getTemplateConfiguration().getMetadataTemplates();
        }
        model.put("templates", templates);
        model.put("isContentTemplates", isContentTemplates);

        return new ModelAndView("/WEB-INF/jsp/admin/templates/listcontenttemplates.jsp", model);
    }

    public void setTemplateConfigurationCache(TemplateConfigurationCache templateConfigurationCache) {
        this.templateConfigurationCache = templateConfigurationCache;
    }
}
