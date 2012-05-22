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

package no.kantega.search.criteria;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: Jan 8, 2009
 * Time: 7:43:55 AM
 *
 * @author Tarje Killingberg
 */
public abstract class BooleanCriterion extends AbstractCriterion {

    private static final String SOURCE = BooleanCriterion.class.getName();

    private List<Criterion> criteria;


    public BooleanCriterion() {
        criteria = new ArrayList<Criterion>();
    }

    /**
     * Returnerer operatoren som skal benyttes for å kombinere Criterion-objektetene i dette BooleanCriterion-objektet.
     * @return operatoren som skal benyttes for å kombinere Criterion-objektetene i dette BooleanCriterion-objektet
     */
    protected abstract BooleanClause.Occur getInnerOperator();

    public void add(Criterion criterion) {
        criteria.add(criterion);
    }

    /**
     * {@inheritDoc}
     */
    public Query getQuery() {
        BooleanQuery query = new BooleanQuery();
        for (Criterion c : criteria) {
            query.add(c.getQuery(), getInnerOperator());
        }
        return query;
    }

}
