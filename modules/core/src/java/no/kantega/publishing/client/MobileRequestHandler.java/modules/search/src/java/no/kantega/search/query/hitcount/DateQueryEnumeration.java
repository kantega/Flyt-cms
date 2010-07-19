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

import no.kantega.commons.log.Log;
import no.kantega.search.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.ConstantScoreRangeQuery;
import org.apache.lucene.search.Query;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 */
public class DateQueryEnumeration extends QueryEnumeration {
    private static final String SOURCE = DateQueryEnumeration.class.getName();

    private String[] terms;
    private int currentIdx = -2;
    private int nofBins = 0;


    public DateQueryEnumeration(IndexReader reader, String field, int nofBins, String lowerTerm, String upperTerm) throws IOException {
        super(reader, field, new String[0], true);
        this.nofBins = nofBins;
        try {
            createTermsArray();
        } catch (ParseException e) {
            terms = new String[0];
            Log.error(SOURCE, e, null, null);
        }
    }

    public boolean next() throws IOException {
        if (currentIdx < terms.length - 2) {
            currentIdx += 2;
            return true;
        } else {
            return false;
        }
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
        /**
         * Intervaller:
         * sist uke
         * sist måned
         * sist år
         * 2 foregående år
         * > 3år
         */
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
        Calendar calToday = new GregorianCalendar();
        int day = calToday.get(Calendar.DAY_OF_MONTH);
        int mnt = calToday.get(Calendar.MONTH);
        int year = calToday.get(Calendar.YEAR);
        int hour = calToday.get(Calendar.HOUR);
        int minute = calToday.get(Calendar.MINUTE);

        // hack
        if (hour == 0 && minute < 10) {
            calToday = new GregorianCalendar(year, mnt, day);
            calToday.add(Calendar.MINUTE, -20);
        } else {
            calToday = new GregorianCalendar(year, mnt, day, 23, 59, 59);
        }

        Date today = calToday.getTime();
        Calendar c = Calendar.getInstance();
        c.setTime(today); // dagens dato

        terms = new String[10]; // antall intervall

        // sist uke
        terms[1] = format.format(c.getTime());
        c.add(Calendar.DATE, -8);
        terms[0]  = format.format(c.getTime());

        // sist mnd
        c.setTime(today);
        terms[3] = format.format(c.getTime());
        c.add(Calendar.MONTH, -1);
        terms[2] = format.format(c.getTime());

        // sist år
        c.setTime(today);
        terms[5] = format.format(c.getTime());
        c.add(Calendar.YEAR, -1);
        terms[4] = format.format(c.getTime());

        // siste 2 år
        c.setTime(today);
        c.add(Calendar.YEAR, -1);
        terms[7] = format.format(c.getTime());
        c.add(Calendar.YEAR, -2);
        terms[6] = format.format(c.getTime());

        // eldre enn 3 år
        c.setTime(today);
        c.add(Calendar.YEAR, -3);
        terms[9] = format.format(c.getTime());
        c.add(Calendar.YEAR, -(year - 1970));
        terms[8] = format.format(c.getTime());
    }
}
