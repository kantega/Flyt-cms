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

import no.kantega.search.query.SearchQuery;
import no.kantega.search.query.SuggestionQuery;
import no.kantega.search.result.SearchResult;
import no.kantega.search.result.Suggestion;

import java.util.List;

/**
 * Date: Dec 5, 2008
 * Time: 8:44:45 AM
 *
 * @author Tarje Killingberg
 */
public interface Searcher {

    
    /**
     * Utfører et søk basert på det gitte SearchQuery'et.
     *
     * @param query et SearchQuery-objekt som beskriver søket som skal utføres.
     * @return et SearchResult-objekt.
     */
    public SearchResult search(SearchQuery query);

    /**
     * @param query et SuggestionQuery-objekt
     * @return en liste med Suggestion-objekter, sortert etter antall treff
     */
    public List<Suggestion> suggest(SuggestionQuery query);

}
