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

package no.kantega.search.query.hitcount;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import java.io.IOException;
import java.util.Arrays;

import no.kantega.search.query.hitcount.HitCountQuery;

/**
 * Date: Jan 16, 2009
 * Time: 2:09:13 PM
 *
 * @author Tarje Killingberg
 */
public class HitCountQueryDefaultImpl implements HitCountQuery {

    private static final String SOURCE = HitCountQueryDefaultImpl.class.getName();

    private String field;
    private String[] terms;
    private boolean ignoreOther;


    public HitCountQueryDefaultImpl(String field) {
        this(field, new String[0], true);
    }

    public HitCountQueryDefaultImpl(String field, String[] terms) {
        this(field, terms, true);
    }

    /**
     * Oppretter et HitCountQuery-objekt.
     *
     * @param field navnet på et felt i indeksen
     * @param terms en liste med termer i indeksen det skal søkes etter antall treff for
     * @param ignoreOther hvis false så summeres og returneres antall treff for termer som ikke finnes i det gitte
     *                    array'et med termer
     */
    public HitCountQueryDefaultImpl(String field, String[] terms, boolean ignoreOther) {
        this.field = field;
        Arrays.sort(terms);
        this.terms = terms;
        this.ignoreOther = ignoreOther;
    }

    public String getField() {
        return field;
    }

    public String[] getTerms() {
        return terms;
    }

    public boolean isIgnoreOther() {
        return ignoreOther;
    }

    public QueryEnumeration getQueryEnumeration(IndexReader reader) throws IOException {
        return new QueryEnumeration(reader, field, terms, ignoreOther);
    }

}
