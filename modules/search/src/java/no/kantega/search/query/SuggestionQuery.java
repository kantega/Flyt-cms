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

package no.kantega.search.query;

import no.kantega.search.core.SuggestionProvider;

/**
 * Date: Jan 19, 2009
 * Time: 9:00:10 AM
 *
 * @author Tarje Killingberg
 */
public interface SuggestionQuery {


    /**
     * Returnerer feltet i indeksen det skal søkes etter Suggestions i.
     * @return feltet i indeksen det skal søkes etter Suggestions i
     */
    public String getField();

    /**
     * Returneres teksten det skal søkes etter Suggestions for.
     * @return teksten det skal søkes etter Suggestions for
     */
    public String getText();

    /**
     * Returnerer maksimalt antall Suggestions som skal finnes.
     * @return maksimalt antall Suggestions som skal finnes
     */
    public int getMax();

    /**
     * Returnerer en instans av en klasse som implementerer SuggestionsProvider som kan brukes til å utføre søk på
     * dette SearchQuery'et. Denne instansen må være ferdig initialisert og klar til å brukes.
     *
     * @param indexManager et IndexManager-objekt
     * @return en instans av en klasse som implementerer SuggestionsProvider
     */
    public SuggestionProvider getSuggestionsProvider(IndexManager indexManager);

}
