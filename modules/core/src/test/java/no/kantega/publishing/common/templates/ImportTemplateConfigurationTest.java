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

package no.kantega.publishing.common.templates;

import junit.framework.TestCase;
import no.kantega.publishing.common.data.TemplateConfiguration;
import no.kantega.publishing.common.util.templates.XStreamTemplateConfigurationFactory;
import org.springframework.core.io.ClassPathResource;

public class ImportTemplateConfigurationTest extends TestCase {
    public void testImportConfiguration() {
        XStreamTemplateConfigurationFactory factory = new XStreamTemplateConfigurationFactory();
        factory.setTemplateConfig(new ClassPathResource("test-templateconfig.xml"));
        TemplateConfiguration config = factory.getConfiguration();

        assertEquals(config.getAssociationCategories().size(), 4);
        assertEquals(config.getContentTemplates().size(), 3);
        assertEquals(config.getDisplayTemplates().size(), 4);
    }
}
