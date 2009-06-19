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

import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;

import java.io.File;

/**
 *
 */
public class RelativeFileSystemResourceLoader extends FileSystemResourceLoader {
    private File directory;

    public RelativeFileSystemResourceLoader(File directory) {
        this.directory = directory;
    }

    @Override
    protected Resource getResourceByPath(String path) {
        return super.getResource("file:" +new File(directory, path).getAbsolutePath());
    }
}
