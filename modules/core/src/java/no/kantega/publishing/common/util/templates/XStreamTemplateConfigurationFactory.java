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
import no.kantega.publishing.common.data.InputStreamSource;
import no.kantega.publishing.common.data.TemplateConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class XStreamTemplateConfigurationFactory implements TemplateConfigurationFactory {
    private static final Logger log = LoggerFactory.getLogger(XStreamTemplateConfigurationFactory.class);

    private InputStreamSource inputStreamSource;

    public TemplateConfiguration getConfiguration() {
        XStream xstream = XStreamTemplateHelper.getXStream();

        TemplateConfiguration templateConfiguration = new TemplateConfiguration();

        try {
            InputStream is = inputStreamSource.getInputStream();
            xstream.fromXML(inputStreamSource.getInputStream(), templateConfiguration);
            is.close();
        } catch (IOException e) {
            log.error("", e);
        }

        return templateConfiguration;
    }


    public void setInputStreamSource(InputStreamSource inputStreamSource) {
        this.inputStreamSource = inputStreamSource;
    }
}
