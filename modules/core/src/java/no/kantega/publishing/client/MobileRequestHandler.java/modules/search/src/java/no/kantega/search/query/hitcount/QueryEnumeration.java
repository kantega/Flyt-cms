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

/**
 * Klasse for å iterere over Query'ene til HitCountQuery-objekter.
 *
 * Date: Jan 23, 2009
 * Time: 9:11:36 AM
 *
 * @author Tarje Killingberg
 */
public class QueryEnumeration {

    private static final String SOURCE = QueryEnumeration.class.getName();

    private TermEnumeration termEnumer;
    private String currentTerm = null;
    private String fieldname;


    public QueryEnumeration(IndexReader reader, String fieldname, String[] terms, boolean ignoreOther) throws IOException {
        this.fieldname = fieldname;
        if (terms.length > 0 && ignoreOther) {
            termEnumer = new TermArrayEnumeration(terms);
        } else {
            termEnumer = new TermEnumEnumeration(fieldname, reader);
        }
    }

    public boolean next() throws IOException {
        if (termEnumer.hasMoreTerms()) {
            currentTerm = termEnumer.nextTerm();
        } else {
            currentTerm = null;
        }
        return currentTerm != null;
    }

    public Query query() {
        return new TermQuery(new Term(fieldname, currentTerm));
    }

    public String term() {
        return currentTerm;
    }


    /**
     * Interface som definerer metoder for å iterere over et sett med termer.
     */
    private interface TermEnumeration {


        public boolean hasMoreTerms();

        public String nextTerm() throws IOException;

    }


    /**
     * TermEnumeration-implementasjon som itererer over et gitt array med termer.
     */
    protected class TermArrayEnumeration implements TermEnumeration {

        private String[] termArray;
        private int i;


        public TermArrayEnumeration(String[] termArray) {
            this.termArray = termArray;
            i = 0;
        }

        public boolean hasMoreTerms() {
            return i < termArray.length;
        }

        public String nextTerm() {
            String retVal = termArray[i];
            i++;
            return retVal;
        }

    }


    /**
     * TermEnumeration-implementasjon som itererer over alle termer i et gitt felt i indeksen.
     */
    protected class TermEnumEnumeration implements TermEnumeration {

        private String fieldname;
        private TermEnum termEnum;


        public TermEnumEnumeration(String fieldname, IndexReader reader) throws IOException {
            this.fieldname = fieldname;
            termEnum = reader.terms(new Term(fieldname, ""));
        }

        public boolean hasMoreTerms() {
            return termEnum.term() != null && termEnum.term().field() == fieldname;
        }

        public String nextTerm() throws IOException {
            String retVal = termEnum.term().text();
            termEnum.next();
            return retVal;
        }

    }

}
