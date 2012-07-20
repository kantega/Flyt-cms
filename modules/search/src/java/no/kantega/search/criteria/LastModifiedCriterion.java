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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Criterion som søker etter dokumenter som sist er endret innenfor et gitt tidsintervall.
 *
 * Date: Dec 2, 2008
 * Time: 9:36:47 AM
 *
 * @author Tarje Killingberg
 */
public class LastModifiedCriterion extends Criterion {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-ddTHH:mm:ssZ");
    private final String lastModifiedFrom;
    private final String lastModifiedTo;

    /**
     * Et av argumentene kan være null, men ikke begge.
     *
     * @param lastModifiedFrom fra-dato. Kan være null.
     * @param lastModifiedTo til-dato. Kan være null.
     * @throws IllegalArgumentException if both arguments are null.
     */
    public LastModifiedCriterion(Date lastModifiedFrom, Date lastModifiedTo) throws IllegalArgumentException {
        if(lastModifiedFrom == null && lastModifiedTo == null) throw new IllegalArgumentException("Both lastModifiedFrom and lastModifiedTo cannot be null");

        this.lastModifiedFrom = wildcardOrDate(lastModifiedFrom);
        this.lastModifiedTo = wildcardOrDate(lastModifiedTo);
    }

    private String wildcardOrDate(Date date) {
        if(date == null){
            return "*";
        } else {
          return dateFormat.format(date);
        }
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


    @Override
    public String getCriterionAsString() {
        return Fields.LAST_MODIFIED +":[" + lastModifiedFrom + " TO " + lastModifiedTo + "]";
    }
}
