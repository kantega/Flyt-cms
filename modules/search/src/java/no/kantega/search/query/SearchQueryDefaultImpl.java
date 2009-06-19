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

import no.kantega.search.core.SearchHandler;
import no.kantega.search.core.SearchHandlerDefaultImpl;
import no.kantega.search.criteria.Criterion;
import no.kantega.search.index.IndexManager;
import no.kantega.commons.log.Log;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.search.BooleanQuery;

/**
 * Date: Dec 3, 2008
 * Time: 8:14:47 AM
 *
 * @author Tarje Killingberg
 */
public class SearchQueryDefaultImpl implements SearchQuery {

    private static final String SOURCE = SearchQueryDefaultImpl.class.getName();

    private List<Criterion> criteria;
    private List<Criterion> filters;
    private int maxHits = 1000;


    public SearchQueryDefaultImpl() {
        criteria = new ArrayList<Criterion>();
        filters = new ArrayList<Criterion>();
    }

    /**
     * {@inheritDoc}
     */
    public List<Criterion> getCriteria() {
        return criteria;
    }

    /**
     * {@inheritDoc}
     */
    public List<Criterion> getFilterCriteria() {
        return filters;
    }

    /**
     * {@inheritDoc}
     */
    public SearchHandler getSearchHandler(IndexManager indexManager) {
        SearchHandlerDefaultImpl defaultSearcher = new SearchHandlerDefaultImpl();
        defaultSearcher.setIndexManager(indexManager);
        return defaultSearcher;
    }

    /**
     * {@inheritDoc}
     */
    public int getMaxHits() {
        return maxHits;
    }

    public void setMaxHits(int maxHits) {
        this.maxHits = maxHits;
    }

    public void addCriterion(Criterion criterion) {
        criteria.add(criterion);
    }

    public void addFilterCriterion(Criterion criterion) {
        filters.add(criterion);
    }

    @Override
    public String toString() {
        BooleanQuery query = new BooleanQuery();
        for (Criterion criterion : criteria) {
            query.add(criterion.getQuery(), criterion.getOperator());
        }
        for (Criterion criterion : filters) {
            query.add(criterion.getQuery(), criterion.getOperator());
        }
        return query.toString();
    }

}
