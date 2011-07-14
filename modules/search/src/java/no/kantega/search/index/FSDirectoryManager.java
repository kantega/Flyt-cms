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
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import no.kantega.search.index.config.LuceneConfiguration;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class FSDirectoryManager implements DirectoryManager {
    private LuceneConfiguration luceneConfiguration;

    public Directory getDirectory(String id) {
        File iFile;
        if (luceneConfiguration.getIndexDirectory().equals("")) {
            iFile = new File(id);
        } else {
            iFile = new File(luceneConfiguration.getIndexDirectory() + "/" + id);
        }



        try {
            final boolean exists = iFile.exists();
            final FSDirectory directory = FSDirectory.open(iFile);
            if(!exists) {
                new IndexWriter(directory, new StandardAnalyzer(Version.LUCENE_29), true, IndexWriter.MaxFieldLength.UNLIMITED).close();
            }
            return directory;
        } catch (IOException e) {
            throw new RuntimeException("Can't open index directory for index " + id + " in directory " + iFile, e);
        }
    }

    public void setLuceneConfiguration(LuceneConfiguration luceneConfiguration) {
        this.luceneConfiguration = luceneConfiguration;
    }
}
