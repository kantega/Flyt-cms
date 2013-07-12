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

import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.common.cache.TemplateConfigurationCache;
import no.kantega.publishing.common.data.ContentTemplate;
import no.kantega.publishing.common.data.TemplateConfiguration;
import no.kantega.publishing.common.data.TemplateConfigurationValidationError;
import no.kantega.publishing.common.data.enums.AttributeDataType;
import no.kantega.publishing.common.util.templates.ContentTemplateValidator;
import no.kantega.publishing.common.util.templates.TemplateConfigurationFactory;
import no.kantega.publishing.common.util.templates.TemplateConfigurationValidator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
*/
public class ReloadTemplateConfigurationAction extends AbstractController {
    private TemplateConfigurationFactory templateConfigurationFactory;
    private TemplateConfigurationValidator  templateConfigurationValidator;
    private TemplateConfigurationCache  templateConfigurationCache;
    private ContentTemplateValidator templateValidator;

    private String view;
    
    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();

        if (request.getMethod().equalsIgnoreCase("POST")) {
            TemplateConfiguration config =  templateConfigurationFactory.getConfiguration();

            List<TemplateConfigurationValidationError> errors = new ArrayList<TemplateConfigurationValidationError>();
            errors.addAll(templateConfigurationValidator.validate(config));

            // Validate content templates
            for (ContentTemplate ct : config.getContentTemplates()) {
                errors.addAll(templateValidator.validate(ct, AttributeDataType.CONTENT_DATA));
            }

            // Validate metadata templates
            for (ContentTemplate ct : config.getMetadataTemplates()) {
                errors.addAll(templateValidator.validate(ct, AttributeDataType.META_DATA));
            }

            if (errors.size() == 0) {
                // Update configuration
                 templateConfigurationCache.updateCache();

                // Update database with updated values
                ContentAO.updateContentFromTemplates(config);

                model.put("updateSuccess", true);
            } else {
                model.put("errors", errors);
            }
        }

        model.put("templateConfiguration", templateConfigurationCache.getTemplateConfiguration());

        return new ModelAndView(view, model);
    }

    public void setTemplateConfigurationFactory(TemplateConfigurationFactory configurationFactory) {
        this.templateConfigurationFactory = configurationFactory;
    }

    public void setTemplateConfigurationValidator(TemplateConfigurationValidator configurationValidator) {
        this.templateConfigurationValidator = configurationValidator;
    }

    public void setTemplateConfigurationCache(TemplateConfigurationCache configurationCache) {
        this.templateConfigurationCache = configurationCache;
    }

    public void setTemplateValidator(ContentTemplateValidator templateValidator) {
        this.templateValidator = templateValidator;
    }

    public void setView(String view) {
        this.view = view;
    }
}
