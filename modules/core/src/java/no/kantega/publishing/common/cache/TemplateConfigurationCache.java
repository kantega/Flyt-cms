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

package no.kantega.publishing.common.cache;

import no.kantega.commons.exception.SystemException;
import no.kantega.commons.util.LocaleLabels;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.util.templates.ContentTemplateReader;
import no.kantega.publishing.common.util.templates.TemplateConfigurationFactory;
import no.kantega.publishing.common.util.templates.TemplateConfigurationValidator;
import no.kantega.publishing.spring.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.*;

/**
 * User: Anders Skar, Kantega AS
 * Date: Dec 17, 2008
 * Time: 1:41:53 PM
 */
public class TemplateConfigurationCache {
    private static final Logger log = LoggerFactory.getLogger(TemplateConfigurationCache.class);

    private Date lastUpdate;
    private TemplateConfiguration configuration;
    private TemplateConfigurationFactory configurationFactory;
    private TemplateConfigurationValidator configurationValidator;
    private ContentTemplateReader contentTemplateReader;

    public TemplateConfiguration getTemplateConfiguration() throws SystemException {
        if ((configuration == null) || (Aksess.getDatabaseCacheTimeout() > 0 && lastUpdate.getTime() + (Aksess.getDatabaseCacheTimeout()) < new Date().getTime())) {
            updateCache();
        }
        return configuration;
    }

    public Date getLastUpdate() {
        if (lastUpdate == null) {
            updateCache();
        }
        return lastUpdate;
    }


    public static TemplateConfigurationCache getInstance() {
        ApplicationContext context = RootContext.getInstance();
        Map beans = context.getBeansOfType(TemplateConfigurationCache.class);
        return (TemplateConfigurationCache) beans.values().iterator().next();
    }

    public synchronized void updateCache() {
        log.debug( "Updating template configuration cache");
        configuration = configurationFactory.getConfiguration();

        // Sort lists
        Collections.sort(configuration.getContentTemplates(), new Comparator<ContentTemplate>() {
            public int compare(ContentTemplate t1, ContentTemplate t2) {
                return t1.getName().compareTo(t2.getName());
            }
        });
        Collections.sort(configuration.getDisplayTemplates(), new Comparator<DisplayTemplate>() {
            public int compare(DisplayTemplate t1, DisplayTemplate t2) {
                return t1.getName().compareTo(t2.getName());
            }
        });
        Collections.sort(configuration.getDocumentTypes(), new Comparator<DocumentType>() {
            public int compare(DocumentType t1, DocumentType t2) {
                return t1.getName().compareTo(t2.getName());
            }
        });

        // Load content templates from file
        for (ContentTemplate contentTemplate : configuration.getContentTemplates()) {
            List<TemplateConfigurationValidationError> templateErrors = contentTemplateReader.updateContentTemplateFromTemplateFile(contentTemplate);
            for (TemplateConfigurationValidationError error : templateErrors) {
                String msg = LocaleLabels.getLabel(error.getMessage(), new Locale("en", "EN"));
                log.error( "Error in template: " + error.getObject() + ":" + msg + ":" + error.getData());
            }
        }

        // Validate
        List <TemplateConfigurationValidationError> errors = configurationValidator.validate(configuration);
        for (TemplateConfigurationValidationError error : errors) {
            String msg = LocaleLabels.getLabel(error.getMessage(), new Locale("en", "EN"));
            log.error( "Error in templateconfig: " + error.getObject() + ":" + msg + ":" + error.getData());
        }

        if (configuration.getSites().size() == 0) {
            log.error( "No sites defined in aksess-templateconfig.xml");
        }

        if (configuration.getAssociationCategories().size() == 0) {
            log.error( "No association categories defined in aksess-templateconfig.xml");
        }

        if (configuration.getContentTemplates().size() == 0) {
            log.error( "No content templates defined in aksess-templateconfig.xml");
        }

        if (configuration.getDisplayTemplates().size() == 0) {
            log.error( "No display templates defined in aksess-templateconfig.xml");
        }

        lastUpdate = new Date();
    }

    public synchronized void updateContentTemplateFromFile(ContentTemplate contentTemplate) {
        List<TemplateConfigurationValidationError> templateErrors = contentTemplateReader.updateContentTemplateFromTemplateFile(contentTemplate);
        for (TemplateConfigurationValidationError error : templateErrors) {
            String msg = LocaleLabels.getLabel(error.getMessage(), new Locale("en", "EN"));
            log.error( "Error in template: " + error.getObject() + ":" + msg + ":" + error.getData());
        }
    }

    public void setConfigurationFactory(TemplateConfigurationFactory configurationFactory) {
        this.configurationFactory = configurationFactory;
    }

    public void setConfigurationValidator(TemplateConfigurationValidator configurationValidator) {
        this.configurationValidator = configurationValidator;
    }

    public void setContentTemplateReader(ContentTemplateReader contentTemplateReader) {
        this.contentTemplateReader = contentTemplateReader;
    }
}
