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

package no.kantega.publishing.common.data;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;


/**
 * ResourceLoader that use prefix when loading.
 */
public class ServletResourceLoader extends DefaultResourceLoader implements ResourceLoaderAware {
    private String prefix;
    private ResourceLoader resourceLoader;

    @Override
    protected Resource getResourceByPath(String name) {
        String lookupName = prefix == null ? name : getWithPrefix(name);
        return resourceLoader.getResource(lookupName);
    }

    private String getWithPrefix(String name) {
        String normalized = name.replace('\\', '/'); // name contains \ on windows.
        if (normalized.contains(prefix)){
            int removeBefore = normalized.indexOf(prefix);
            return normalized.substring(removeBefore, normalized.length());
        }else {
            return prefix + name;
        }
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
