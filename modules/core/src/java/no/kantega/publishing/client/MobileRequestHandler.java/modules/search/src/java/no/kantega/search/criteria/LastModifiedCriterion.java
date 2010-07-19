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
import org.apache.lucene.document.DateTools;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeQuery;
import org.apache.lucene.search.ConstantScoreRangeQuery;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Criterion som søker etter dokumenter som sist er endret innenfor et gitt tidsintervall.
 *
 * Date: Dec 2, 2008
 * Time: 9:36:47 AM
 *
 * @author Tarje Killingberg
 */
public class LastModifiedCriterion extends AbstractCriterion {

    private static final String SOURCE = LastModifiedCriterion.class.getName();

    private String lastModifiedFrom;
    private String lastModifiedTo;


    public String getLastModifiedFrom() {
        return lastModifiedFrom;
    }

    public String getLastModifiedTo() {
        return lastModifiedTo;
    }

    /**
     * Et av argumentene kan være null, men ikke begge.
     *
     * @param lastModifiedFrom fra-dato. Kan være null.
     * @param lastModifiedTo til-dato. Kan være null.
     */
    public LastModifiedCriterion(Date lastModifiedFrom, Date lastModifiedTo) {
        this.lastModifiedFrom = DateTools.dateToString(lastModifiedFrom, DateTools.Resolution.MINUTE);
        this.lastModifiedTo = DateTools.dateToString(lastModifiedTo, DateTools.Resolution.MINUTE);
    }

    /**
     * Et av argumentene kan være null, men ikke begge.
     * Formatet på argumentene må være som generert av DateTools.dateToString(Date, DateTools.Resolution.MINUTE)
     *
     * @param lastModifiedFrom fra-dato. Kan være null.
     * @param lastModifiedTo til-dato. Kan være null.
     */
    public LastModifiedCriterion(String lastModifiedFrom, String lastModifiedTo) {
        this.lastModifiedFrom = lastModifiedFrom;
        this.lastModifiedTo = lastModifiedTo;
    }

    /**
     * {@inheritDoc}
     */
    public Query getQuery() {
        return new ConstantScoreRangeQuery(Fields.LAST_MODIFIED, lastModifiedFrom, lastModifiedTo, true, true);
    }

}
