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


package no.kantega.publishing.admin.content.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 *  Resolves !ENTITY system references in XML files loaded using ResourceLoader.
 *  Supports includes from the same directory as the xml it is included in, as well as
 *  resources relative to it.
 */
public class ResourceLoaderEntityResolver implements EntityResolver {
    private ResourceLoader resourceLoader;

    public ResourceLoaderEntityResolver(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        if (systemId != null) {
            Resource resource = resourceLoader.getResource(systemId);
            if (resource != null) {
                InputSource inputSource = new InputSource(resource.getInputStream());
                inputSource.setSystemId(systemId);
                return inputSource;
            }
        }
        return null;
    }
}
