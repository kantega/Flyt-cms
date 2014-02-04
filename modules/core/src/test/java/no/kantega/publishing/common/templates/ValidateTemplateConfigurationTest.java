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
import org.apache.commons.collections.Predicate;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

import static org.apache.commons.collections.CollectionUtils.select;
import static org.junit.Assert.assertEquals;

public class ValidateTemplateConfigurationTest {
    @Test
    public void testValidate() {
        XStreamTemplateConfigurationFactory factory = new XStreamTemplateConfigurationFactory();
        factory.setTemplateConfig(new ClassPathResource("test-templateconfig.xml"));
        TemplateConfiguration config = factory.getConfiguration();

        TemplateConfigurationValidator validator = new TemplateConfigurationValidator();
        List<TemplateConfigurationValidationError> errors = validator.validate(config);

        assertEquals(4, errors.size());
        assertEquals(1, select(errors, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                TemplateConfigurationValidationError object1 = (TemplateConfigurationValidationError) object;
                return object1.getMessage().equals("aksess.templateconfig.error.duplicateid")
                        && ((TemplateConfigurationValidationError) object).getData().equals("nyhet2(3)");
            }
        }).size());

        assertEquals(2, select(errors, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                TemplateConfigurationValidationError object1 = (TemplateConfigurationValidationError) object;
                return object1.getMessage().equals("aksess.templateconfig.error.invalidreferencetocontenttemplate")
                        && ((TemplateConfigurationValidationError) object).getData().equals("liste med nyheterX(0)");
            }
        }).size());

        assertEquals(1, select(errors, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                TemplateConfigurationValidationError object1 = (TemplateConfigurationValidationError) object;
                return object1.getMessage().equals("aksess.templateconfig.error.invalidreferencetodocumenttype")
                        && ((TemplateConfigurationValidationError) object).getData().equals("klasseX(0)");
            }
        }).size());
    }
}
