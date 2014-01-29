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

package no.kantega.publishing.common.util.templates;

import com.thoughtworks.xstream.XStream;
import no.kantega.publishing.common.data.TemplateConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

public class XStreamTemplateConfigurationFactory implements TemplateConfigurationFactory {
    private static final Logger log = LoggerFactory.getLogger(XStreamTemplateConfigurationFactory.class);

    private Resource templateConfig;

    public TemplateConfiguration getConfiguration() {
        XStream xstream = XStreamTemplateHelper.getXStream();

        TemplateConfiguration templateConfiguration = new TemplateConfiguration();

        try (InputStream is = templateConfig.getInputStream()) {
            xstream.fromXML(is, templateConfiguration);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read template configuration", e);
        }

        return templateConfiguration;
    }


    public void setTemplateConfig(Resource resource) {
        this.templateConfig = resource;
    }
}
