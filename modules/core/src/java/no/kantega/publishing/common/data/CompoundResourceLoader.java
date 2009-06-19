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

import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * User: Anders Skar, Kantega AS
 * Date: May 28, 2009
 * Time: 12:31:00 PM
 */
public class CompoundResourceLoader extends DefaultResourceLoader {
    private final List<ResourceLoader> sources;
    
    public CompoundResourceLoader(ResourceLoader... sources) {
        this.sources = new ArrayList<ResourceLoader>(Arrays.asList(sources));
    }


    @Override
    protected Resource getResourceByPath(String name) {
        for (ResourceLoader source : sources) {
            Resource resource = source.getResource(name);
            if (resource != null && resource.exists()) {
                return resource;
            }
        }

        return null;
    }

    public void addResourceLoader(ResourceLoader a) {
        sources.add(a);
    }
}
