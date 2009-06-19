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

import no.kantega.search.index.Fields;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;

/**
 * Date: Jan 30, 2009
 * Time: 2:54:04 AM
 *
 * @author Tarje Killingberg
 */
public class PhraseCriterion extends AbstractCriterion {

    public final static String DEFAULT_FIELDNAME = Fields.CONTENT_UNSTEMMED;
    private static final String SOURCE = PhraseCriterion.class.getName();

    private String fieldname;
    private String text;


    public PhraseCriterion(String text) {
        this(DEFAULT_FIELDNAME, text);
    }

    public PhraseCriterion(String fieldname, String text) {
        this.fieldname = fieldname;
        this.text = text;
    }

    /**
     * {@inheritDoc}
     */
    public Query getQuery() {
        PhraseQuery query = new PhraseQuery();
        String[] terms = text.split(" ");
        for (String term : terms) {
            query.add(new Term(fieldname, term));
        }
        return query;
    }

}
