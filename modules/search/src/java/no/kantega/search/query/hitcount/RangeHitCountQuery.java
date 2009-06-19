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
import java.text.ParseException;

/**
 * Date: Jan 23, 2009
 * Time: 9:18:56 AM
 *
 * @author Tarje Killingberg
 */
public class RangeHitCountQuery implements HitCountQuery {

    private static final String SOURCE = RangeHitCountQuery.class.getName();

    private String field;
    private int nofBins;
    private String lowerTerm;
    private String upperTerm;


    public RangeHitCountQuery(String field, int nofBins) {
        this(field, nofBins, null, null);
    }

    public RangeHitCountQuery(String field, int nofBins, String lowerTerm,String upperTerm) {
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
    public RangeQueryEnumeration getQueryEnumeration(IndexReader reader) throws IOException {
        return new RangeQueryEnumeration(reader, getField(), nofBins, lowerTerm, upperTerm);
    }

}
