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
import no.kantega.search.criteria.TextCriterion;
import no.kantega.search.query.hitcount.HitCountQuery;
import org.apache.lucene.analysis.Analyzer;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides simple implementations of the methods from {@link SearchField}
 * usable for regular searching in a field.
 *
 * @author Tarje Killingberg
 */
public abstract class AbstractSearchField implements SearchField {


    /**
     * {@inheritDoc}
     */
    public List<Criterion> getQueryCriteria(String queryPhrase, Analyzer analyzer) {
        List<Criterion> criteria = new ArrayList<Criterion>();
        criteria.add(new TextCriterion(getFieldname(), queryPhrase, analyzer));
        return criteria;
    }

    /**
     * {@inheritDoc}
     *
     * This implementation just returns <tt>null</tt>.
     */
    public List<Criterion> getFilterCriteria(SearchServiceQuery query) {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * This implementation just returns <tt>null</tt>.
     */
    public List<HitCountQuery> getHitCountQueries(SearchServiceQuery query, HttpServletRequest request, Content content) {
        return null;
    }

}
