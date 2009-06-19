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
import no.kantega.search.index.Fields;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

/**
 * Date: Jan 8, 2009
 * Time: 8:20:42 AM
 *
 * @author Tarje Killingberg
 */
public class ContentTypeCriterion extends AbstractCriterion {

    private static final String SOURCE = ContentTypeCriterion.class.getName();

    private String[] termArray;


    public ContentTypeCriterion(String term) {
        this(new String[]{ term });
    }

    public ContentTypeCriterion(String[] termArray) {
        this.termArray = termArray;
    }

    /**
     * {@inheritDoc}
     */
    public Query getQuery() {
        BooleanQuery query = new BooleanQuery();
        for (String sTerm : termArray) {
            Term term = new Term(Fields.DOCTYPE, sTerm);
            query.add(new TermQuery(term), BooleanClause.Occur.SHOULD);
        }
        return query;
    }

}
