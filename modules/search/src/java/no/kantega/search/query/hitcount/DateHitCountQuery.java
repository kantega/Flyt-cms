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

import java.io.IOException;

/**
 *
 */
public class DateHitCountQuery  implements HitCountQuery {
    private static final String SOURCE = DateHitCountQuery.class.getName();

    private String field;
    private int nofBins;
    private String lowerTerm;
    private String upperTerm;


    public DateHitCountQuery(String field, int nofBins) {
        this(field, nofBins, null, null);
    }

    public DateHitCountQuery(String field, int nofBins, String lowerTerm,String upperTerm) {
        this.field = field;
        this.nofBins = nofBins;
        this.lowerTerm = lowerTerm;
        this.upperTerm = upperTerm;
    }

    /**
     * {@inheritDoc}
     */
    public String getField() {
        return field;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getTerms() {
        return new String[0];
    }

    /**
     * {@inheritDoc}
     */
    public boolean isIgnoreOther() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public DateQueryEnumeration getQueryEnumeration(IndexReader reader) throws IOException {
        return new DateQueryEnumeration(reader, getField(), nofBins, lowerTerm, upperTerm);
    }
}
