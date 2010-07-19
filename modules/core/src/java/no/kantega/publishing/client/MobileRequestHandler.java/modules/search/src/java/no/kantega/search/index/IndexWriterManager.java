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

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.Iterator;

import no.kantega.search.index.config.LuceneConfiguration;


public class IndexWriterManager {

    private Map<String, IndexWriter> map = new HashMap<String, IndexWriter>();
    private Logger log = Logger.getLogger(IndexWriterManager.class);

    private DirectoryManager directoryManager;

    public IndexWriter getIndexWriter(String id, boolean create) throws IOException {
        log.debug((create ? "Creating " : "Opening") +" IndexWriter for index " +id );

        IndexWriter iw;
        Analyzer analyzer = IndexManagerImpl.getInstance().getAnalyzerFactory().createInstance();

        if(map.get(id) != null && create) {
            ensureClosed(id);
        }

        if(map.get(id) != null && !create) {
            iw = map.get(id);
        } else {
            iw = new IndexWriter(directoryManager.getDirectory(id), analyzer, create, IndexWriter.MaxFieldLength.UNLIMITED);
            map.put(id, iw);
        }
        return iw;
    }

    public void destroy() throws IOException {
        Collection values = map.values();
        for (Iterator iterator = values.iterator(); iterator.hasNext();) {
            IndexWriter indexWriter = (IndexWriter) iterator.next();
            indexWriter.optimize();
            indexWriter.close();
        }
    }

    public void ensureClosed(String id) throws IOException {
        if(map.get(id) != null) {
           IndexWriter iw = (IndexWriter) map.get(id);
            iw.close();
            map.remove(id);
        }
    }

    public void setDirectoryManager(DirectoryManager directoryManager) {
        this.directoryManager = directoryManager;
    }
}
