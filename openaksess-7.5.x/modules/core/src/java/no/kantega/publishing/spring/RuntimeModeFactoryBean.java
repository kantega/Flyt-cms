/*
 * Copyright 2010 Kantega AS
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

import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;

/**
 *
 */
public class RuntimeModeFactoryBean extends AbstractFactoryBean implements ServletContextAware{
    private ServletContext servletContext;

    @Override
    public Class getObjectType() {
        return RuntimeMode.class;
    }

    @Override
    protected Object createInstance() throws Exception {

        if(System.getProperty("development") != null) {
            return RuntimeMode.DEVELOPMENT;
        } else {
            return RuntimeMode.PRODUCTION;
        }
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
