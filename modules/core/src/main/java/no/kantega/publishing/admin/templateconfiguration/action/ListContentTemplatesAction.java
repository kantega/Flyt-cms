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

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.api.content.attribute.AttributeDataType;
import no.kantega.publishing.common.cache.TemplateConfigurationCache;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class ListContentTemplatesAction extends AbstractController {
    private TemplateConfigurationCache templateConfigurationCache;
    private String view;

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<>();

        RequestParameters param = new RequestParameters(request);
        AttributeDataType attributeDataType = AttributeDataType.CONTENT_DATA;
        if (param.getInt("type") != -1) {
            attributeDataType = AttributeDataType.getDataTypeAsEnum(param.getInt("type"));
        }

        boolean isContentTemplates = true;

        List templates;
        if (attributeDataType == AttributeDataType.CONTENT_DATA) {
            templates = templateConfigurationCache.getTemplateConfiguration().getContentTemplates();
        } else {
            isContentTemplates = false;
            templates = templateConfigurationCache.getTemplateConfiguration().getMetadataTemplates();
        }
        model.put("templates", templates);
        model.put("isContentTemplates", isContentTemplates);

        return new ModelAndView(view, model);
    }

    public void setTemplateConfigurationCache(TemplateConfigurationCache templateConfigurationCache) {
        this.templateConfigurationCache = templateConfigurationCache;
    }

    public void setView(String view) {
        this.view = view;
    }
}
