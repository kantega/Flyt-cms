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
import no.kantega.search.core.SearchHandlerExtendedImpl;
import no.kantega.search.criteria.Criterion;
import no.kantega.search.query.hitcount.HitCountQuery;
import no.kantega.search.result.TermTranslator;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Sort;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: Dec 3, 2008
 * Time: 8:10:02 AM
 *
 * @author Tarje Killingberg
 */
public class SearchQueryExtendedImpl implements SearchQuery {

    private static final String SOURCE = SearchQueryExtendedImpl.class.getName();

    private List<Criterion> criteria;
    private List<Criterion> filters;
    private int maxHits = 1000;
    private TermTranslator termTranslator;
    private List<HitCountQuery> hitCountQueries;
    private Sort sort;


    public SearchQueryExtendedImpl() {
        criteria = new ArrayList<Criterion>();
        filters = new ArrayList<Criterion>();
        hitCountQueries = new ArrayList<HitCountQuery>();
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
        SearchHandlerExtendedImpl extSearchHandler = new SearchHandlerExtendedImpl();
        extSearchHandler.setIndexManager(indexManager);
        return extSearchHandler;
    }

    /**
     * {@inheritDoc}
     */
    public int getMaxHits() {
        return maxHits;
    }

    /**
     * {@inheritDoc}
     */
    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public TermTranslator getTermTranslator() {
        return termTranslator;
    }

    public void setTermTranslator(TermTranslator termTranslator) {
        this.termTranslator = termTranslator;
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

    public List<HitCountQuery> getHitCountQueries() {
        return hitCountQueries;
    }

    public void addHitCountQuery(HitCountQuery query) {
        hitCountQueries.add(query);
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
