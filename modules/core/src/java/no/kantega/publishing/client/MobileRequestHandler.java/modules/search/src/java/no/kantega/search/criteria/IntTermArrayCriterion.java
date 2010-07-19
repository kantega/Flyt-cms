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

import no.kantega.commons.log.Log;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

/**
 * Date: Jan 7, 2009
 * Time: 2:42:25 PM
 *
 * @author Tarje Killingberg
 */
public abstract class IntTermArrayCriterion extends AbstractCriterion {

    private static final String SOURCE = IntTermArrayCriterion.class.getName();

    private int[] termArray;


    public IntTermArrayCriterion(int term) {
        this(new int[]{ term });
    }

    public IntTermArrayCriterion(int[] termArray) {
        this.termArray = termArray;
    }

    protected abstract String getField();

    /**
     * {@inheritDoc}
     */
    public Query getQuery() {
        BooleanQuery query = new BooleanQuery();
        for (int intTerm : termArray) {
            Term term = new Term(getField(), intTerm + "");
            query.add(new TermQuery(term), BooleanClause.Occur.SHOULD);
        }
        return query;
    }

}
