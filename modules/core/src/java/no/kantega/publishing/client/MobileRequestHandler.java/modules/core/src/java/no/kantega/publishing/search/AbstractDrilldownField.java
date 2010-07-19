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

package no.kantega.publishing.search;

import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.search.service.SearchServiceQuery;
import no.kantega.search.criteria.Criterion;
import no.kantega.search.criteria.ExactCriterion;
import no.kantega.search.query.hitcount.HitCountQuery;
import no.kantega.search.query.hitcount.HitCountQueryDefaultImpl;
import org.apache.lucene.analysis.Analyzer;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides simple implementations of the methods from {@link SearchField}
 * usable for hit count queries and drilldown.
 *
 * @author Tarje Killingberg
 */
public abstract class AbstractDrilldownField implements SearchField {


    /**
     * {@inheritDoc}
     *
     * This implementation just returns <tt>null</tt>.
     */
    public List<Criterion> getQueryCriteria(String queryPhrase, Analyzer analyzer) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<Criterion> getFilterCriteria(SearchServiceQuery query) {
        List<Criterion> criteria = new ArrayList<Criterion>();
        String text = query.getStringParam(getFieldname());
        if (text != null) {
            criteria.add(new ExactCriterion(getFieldname(), text));
        }
        return criteria;
    }

    /**
     * {@inheritDoc}
     */
    public List<HitCountQuery> getHitCountQueries(SearchServiceQuery query, HttpServletRequest request, Content content) {
        List<HitCountQuery> hitCountQueries = new ArrayList<HitCountQuery>();
        hitCountQueries.add(new HitCountQueryDefaultImpl(getFieldname()));
        return hitCountQueries;
    }

}
