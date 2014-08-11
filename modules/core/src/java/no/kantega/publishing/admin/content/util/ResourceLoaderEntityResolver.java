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

import java.io.File;
import java.io.IOException;

/**
 *  Resolves !ENTITY system references in XML files loaded using ResourceLoader.
 *  Supports includes from sub directories by the use of «$ROOT/» in the entity declaration.
 */
public class ResourceLoaderEntityResolver implements EntityResolver {
    private ResourceLoader resourceLoader;
    private File referenceDir;

    public ResourceLoaderEntityResolver(ResourceLoader resourceLoader, File referenceDir) {
        this.resourceLoader = resourceLoader;
        this.referenceDir = referenceDir;
    }

    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        if (systemId != null) {
            String filename = systemId;
            if (filename.contains("/")) {
                filename = filename.substring(systemId.lastIndexOf("/") + 1, systemId.length());
            }
            Resource resource = resourceLoader.getResource(referenceDir.getAbsolutePath() + File.separator + filename);
            if (resource != null) {
                InputSource inputSource = new InputSource(resource.getInputStream());
                inputSource.setSystemId(systemId);
                return inputSource;
            }
        }
        return null;
    }
}
