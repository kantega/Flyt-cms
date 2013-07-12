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

package no.kantega.publishing.spring;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.FileSystemResource;

import java.util.Properties;

import no.kantega.commons.configuration.Configuration;


public class ConfigurationPropertiesFactory implements FactoryBean {

    public Object getObject() throws Exception {

        Properties prop = new Properties();

        prop.load(new FileSystemResource(Configuration.getConfigDirectory() + "/aksess.conf").getInputStream());

        return prop;
    }

    public Class getObjectType() {
        return Properties.class;
    }

    public boolean isSingleton() {
        return true;
    }
}
