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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * Factory creating the appropriate <code>RuntimeMode</code> based on
 * whether the system property development is defined.
 */
public class RuntimeModeFactoryBean extends AbstractFactoryBean {
    private static final Logger logger = LoggerFactory.getLogger(RuntimeModeFactoryBean.class);

    @Override
    public Class getObjectType() {
        return RuntimeMode.class;
    }

    @Override
    protected Object createInstance() throws Exception {
        RuntimeMode runtimeMode;
        if(System.getProperty("development") != null) {
            runtimeMode = RuntimeMode.DEVELOPMENT;
        } else {
            runtimeMode = RuntimeMode.PRODUCTION;
        }
        logger.info("Runtimemode: " + runtimeMode);
        return runtimeMode;
    }
}
