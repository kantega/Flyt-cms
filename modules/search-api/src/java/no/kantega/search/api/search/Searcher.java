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

package no.kantega.search.api.search;

import java.util.List;

public interface Searcher {

    
    /**
     * Perform a search based on the SearchQuery
     *
     * @param query - a SearchQuery..
     * @return a SearchResponse containing information about the result of the query.
     */
    public SearchResponse search(SearchQuery query);

    /**
     * @param query a SuggestionQuery
     * @return a list of suggestions based on the query.
     */
    public List<String> suggest(SearchQuery query);

}
