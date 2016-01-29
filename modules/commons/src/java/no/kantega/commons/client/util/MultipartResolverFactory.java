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

package no.kantega.commons.client.util;

import no.kantega.commons.configuration.Configuration;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

public class MultipartResolverFactory extends AbstractFactoryBean {
    private CommonsMultipartResolver multipartResolver;

    private Configuration aksessConfiguration;

    protected Object createInstance() throws Exception {

        int maxSize = aksessConfiguration.getInt("upload.maxsize", 0x4000000);

        multipartResolver.setMaxUploadSize(maxSize);

        return multipartResolver;
    }

    public Class getObjectType() {
        return MultipartResolver.class;
    }

    public void setMultipartResolver(CommonsMultipartResolver multipartResolver) {
        this.multipartResolver = multipartResolver;
    }

    public void setAksessConfiguration(Configuration aksessConfiguration) {
        this.aksessConfiguration = aksessConfiguration;
    }
}
