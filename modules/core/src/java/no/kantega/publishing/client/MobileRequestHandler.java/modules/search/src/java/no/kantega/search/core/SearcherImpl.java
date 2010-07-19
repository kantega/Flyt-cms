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

package no.kantega.search.core;

import no.kantega.commons.log.Log;
import no.kantega.search.index.IndexManager;
import no.kantega.search.query.SearchQuery;
import no.kantega.search.query.SuggestionQuery;
import no.kantega.search.result.SearchResult;
import no.kantega.search.result.Suggestion;
import org.apache.lucene.search.BooleanQuery;

import java.io.IOException;
import java.util.List;

/**
 * Date: Jan 15, 2009
 * Time: 1:35:11 PM
 *
 * @author Tarje Killingberg
 */
public class SearcherImpl implements Searcher {

    private static final String SOURCE = SearcherImpl.class.getName();

    private IndexManager indexManager;


    public SearcherImpl() {
    }

    public SearcherImpl(int maxClauseCount) {
        Log.info(SearcherImpl.class.getSimpleName(), "Setting Lucene's maxClauseCount to " + maxClauseCount + ".", null, null);
        BooleanQuery.setMaxClauseCount(maxClauseCount);
    }

    /**
     * {@inheritDoc}
     */
    public SearchResult search(SearchQuery query) {
        long start = System.currentTimeMillis();
        SearchResult searchResult;
        try {
            SearchHandler searchHandler = query.getSearchHandler(indexManager);
            searchResult = searchHandler.handleSearch(query);
        } catch (IOException e) {
            Log.error(SOURCE, e, "search", null);
            throw new RuntimeException(e);
        }
        Log.info(SOURCE, (System.currentTimeMillis() - start) / 1000d + " sekunder", "search(SearchQuery)", null);
        return searchResult;
    }

    /**
     * {@inheritDoc}
     */
    public List<Suggestion> suggest(SuggestionQuery query) {
        long start = System.currentTimeMillis();
        List<Suggestion> suggestions;
        try {
            SuggestionProvider provider = query.getSuggestionsProvider(indexManager);
            suggestions = provider.provideSuggestions(query);
        } catch (IOException e) {
            Log.error(SOURCE, e, "suggest", null);
            throw new RuntimeException(e);
        }
        Log.info(SOURCE, (System.currentTimeMillis() - start) / 1000d + " sekunder", "suggest(SuggestionQuery)", null);
        return suggestions;
    }

    public void setIndexManager(IndexManager indexManager) {
        this.indexManager = indexManager;
    }

}
