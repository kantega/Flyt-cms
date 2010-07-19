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

import no.kantega.search.index.Fields;
import no.kantega.commons.log.Log;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.ConstantScoreRangeQuery;
import org.apache.lucene.search.Query;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Calendar;

/**
 * Date: Jan 23, 2009
 * Time: 9:16:36 AM
 *
 * @author Tarje Killingberg
 */
public class RangeQueryEnumeration extends QueryEnumeration {

    private static final String SOURCE = RangeQueryEnumeration.class.getName();

    private IndexReader reader;
    private String field;
    private int nofBins;
    private String lowerTerm;
    private String upperTerm;

    private String[] terms;
    private int currentIdx = -1;


    public RangeQueryEnumeration(IndexReader reader, String field, int nofBins, String lowerTerm, String upperTerm) throws IOException {
        super(reader, field, new String[0], true);
        this.reader = reader;
        this.field = field;
        this.nofBins = nofBins;
        this.lowerTerm = lowerTerm;
        this.upperTerm = upperTerm;

        try {
            createTermsArray();
        } catch (ParseException e) {
            terms = new String[0];
            Log.error(SOURCE, e, null, null);
        }
    }

    public boolean next() throws IOException {
        return ++currentIdx < terms.length - 1;
    }

    public Query query() {
        String from = terms[currentIdx];
        String to = terms[currentIdx+1];
        return new ConstantScoreRangeQuery(Fields.LAST_MODIFIED, from, to, true, false);
    }

    public String term() {
        return "[" + terms[currentIdx] + " TO " + terms[currentIdx + 1] + "]";
    }

    private void createTermsArray() throws IOException, ParseException {
        TermEnum termEnum = reader.terms(new Term(field, lowerTerm != null ? lowerTerm : ""));
        String lowerTerm = null;
        String upperTerm = null;

        if (termEnum.term() != null && termEnum.term().field() == field) {
            lowerTerm = termEnum.term().text();
            for ( ; termEnum.term() != null && termEnum.term().field() == field && (this.upperTerm == null || termEnum.term().text().compareTo(this.upperTerm) <= 0); termEnum.next()) {
                upperTerm = termEnum.term().text();
            }
        }

        if (lowerTerm != null && upperTerm != null && !lowerTerm.equals(upperTerm)) {
            long timeSpan = DateTools.stringToTime(upperTerm) - DateTools.stringToTime(lowerTerm);
            int timeSpanMinutes = (int)(timeSpan / 1000 / 60);
            int binTimeMinutes = (timeSpanMinutes / nofBins) + 1;
            Calendar c = Calendar.getInstance();
            c.setTime(DateTools.stringToDate(lowerTerm));
            terms = new String[nofBins + 1];
            terms[0] = lowerTerm;
            for (int i = 1; i < terms.length; i++) {
                c.add(Calendar.MINUTE, binTimeMinutes);
                terms[i] = DateTools.dateToString(c.getTime(), DateTools.Resolution.MINUTE);
            }
        } else {
            terms = new String[0];
        }
    }

}
