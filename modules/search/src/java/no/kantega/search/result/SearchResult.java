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

package no.kantega.search.result;

import java.util.List;

/**
 * Interface som representerer et søkeresultat. Tilbyr en liste med søketreff og tiden det tok å utføre søket.
 *
 * Date: Dec 1, 2008
 * Time: 2:00:08 PM
 *
 * @author Tarje Killingberg
 */
public interface SearchResult {


    /**
     * Returnerer listen med DocumentHit-objekter som dette SearchResult'et representerer.
     * @return en liste med DocumentHit-objekter
     */
    public List<DocumentHit> getDocumentHits();

    /**
     * Returnerer det totale antallet treff i indeksen. Dette er forskjellig fra getDocumentHits().size()
     * når antall treff overstiger maksimumsverdien satt for antall treff.
     * @return det totale antallet treff i indeksen
     */
    public int getNumberOfHits();

    /**
     * Returnerer tiden brukt på å utføre søket som resulterte i dette SearchResult'et - i millisekunder.
     * @return tiden brukt på å utføre søket som resulterte i dette SearchResult'et - i millisekunder
     */
    public long getTime();

    /**
     * Returnerer et QueryInfo-objekt som inneholder informasjon om søket som genererte dette SearchResult'et.
     * @return et QueryInfo-objekt som inneholder informasjon om søket som genererte dette SearchResult'et
     */
    public QueryInfo getQueryInfo();

}
