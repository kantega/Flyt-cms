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

import no.kantega.publishing.common.data.TemplateConfiguration;
import no.kantega.publishing.common.data.TemplateConfigurationValidationError;
import no.kantega.publishing.common.util.templates.TemplateConfigurationValidator;
import no.kantega.publishing.common.util.templates.XStreamTemplateConfigurationFactory;

import java.util.List;

import junit.framework.TestCase;

/**
 * User: Anders Skar, Kantega AS
 * Date: Jan 14, 2009
 * Time: 3:10:43 PM
 */
public class ValidateTemplateConfigurationTest extends TestCase {
    public void testValidate() {
        XStreamTemplateConfigurationFactory factory = new XStreamTemplateConfigurationFactory();
        factory.setInputStreamSource(new XMLFileInputStreamSource());
        TemplateConfiguration config = factory.getConfiguration();

        TemplateConfigurationValidator validator = new TemplateConfigurationValidator();
        List<TemplateConfigurationValidationError> errors = validator.validate(config);

        assertEquals(4, errors.size());
    }
}
