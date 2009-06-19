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

package no.kantega.search.index;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

/**
 *
 */
public class RAMDirectoryManager implements DirectoryManager {
    private Map<String, RAMDirectory> directories = Collections.synchronizedMap(new HashMap<String, RAMDirectory>());

    public Directory getDirectory(String id) {
        if(directories.containsKey(id)) {
            return directories.get(id);
        } else {
            RAMDirectory directory = new RAMDirectory();
            try {
                new IndexWriter(directory, new StandardAnalyzer(), true, IndexWriter.MaxFieldLength.UNLIMITED).close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            directories.put(id, directory);
            return directory;
        }
    }
}
