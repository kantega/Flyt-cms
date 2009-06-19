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

package no.kantega.publishing.search.service;

import no.kantega.search.result.*;

import java.util.List;
import java.util.Map;

/**
 * Date: Jan 28, 2009
 * Time: 8:58:26 AM
 *
 * @author Tarje Killingberg
 */
public class SearchServiceResultImpl implements SearchServiceResult {

    private static final String SOURCE = SearchServiceResultImpl.class.getName();

    private SearchResult searchResult;
    private List<SearchHit> searchHits;
    private Map<String, List<HitCount>> hitCounts;
    private List<Alternative> alternatives;
    private int currentPage;
    private int fromIndex;
    private int toIndex;


    public SearchServiceResultImpl(SearchResult searchResult) {
        this.searchResult = searchResult;
    }

    public SearchResult getSearchResult() {
        return searchResult;
    }

    public List<SearchHit> getSearchHits() {
        return searchHits;
    }

    public void setSearchHits(List<SearchHit> searchHits) {
        this.searchHits = searchHits;
    }

    public Map<String, List<HitCount>> getHitCounts() {
        return hitCounts;
    }

    public void setHitCounts(Map<String, List<HitCount>> hitCounts) {
        this.hitCounts = hitCounts;
    }

    public List<Alternative> getAlternatives() {
        return alternatives;
    }

    public void setAlternatives(List<Alternative> alternatives) {
        this.alternatives = alternatives;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getFromIndex() {
        return fromIndex;
    }

    public void setFromIndex(int fromIndex) {
        this.fromIndex = fromIndex;
    }

    public int getToIndex() {
        return toIndex;
    }

    public void setToIndex(int toIndex) {
        this.toIndex = toIndex;
    }

}
