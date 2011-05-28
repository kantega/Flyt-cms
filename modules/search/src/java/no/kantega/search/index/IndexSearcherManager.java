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

import org.apache.lucene.search.IndexSearcher;

import java.io.IOException;
import java.io.File;

import no.kantega.search.index.config.LuceneConfiguration;
import no.kantega.commons.log.Log;

public class IndexSearcherManager {

    private static final String SOURCE = IndexSearcherManager.class.getName();
    /**
     * The maximum number of milliseconds an IndexSearcher should be open before it is re-opened.
     */
    private static final long SEARCHER_TIMEOUT = 10000;

    private long searcherTimeout = SEARCHER_TIMEOUT;

    private IndexSearcher indexSearcher = null;
    private long timeOpened = 0;

    private DirectoryManager directoryManager;

    public synchronized IndexSearcher getSearcher(String id) throws IOException {
        if (indexSearcher == null) {
            Log.debug(SOURCE, "indexSearcher was null. Opening new instance.", "getSearcher", null);
            openIndexSearcher(id);
        } else if (System.currentTimeMillis() - timeOpened > searcherTimeout) {
            Log.debug(SOURCE, "indexSearcher has been open for " + (System.currentTimeMillis() - timeOpened) / 1000d + " secs. Opening new instance.", "getSearcher", null);
            closeIndexSearcher();
            openIndexSearcher(id);
        }
        return indexSearcher;
    }

    private void openIndexSearcher(String id) throws IOException {
        indexSearcher = new IndexSearcher(directoryManager.getDirectory(id));
        timeOpened = System.currentTimeMillis();
    }

    private void closeIndexSearcher() {
        try {
            indexSearcher.close();
        } catch (IOException e) {
            Log.error(SOURCE, e, "closeIndexSearcher", null);
        }
    }

    public void setDirectoryManager(DirectoryManager directoryManager) {
        this.directoryManager = directoryManager;
    }

    public void setSearcherTimeout(long searcherTimeout) {
        this.searcherTimeout = searcherTimeout;
    }
}
