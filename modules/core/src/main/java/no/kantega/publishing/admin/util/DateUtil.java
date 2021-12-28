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

package no.kantega.publishing.admin.util;

import no.kantega.commons.util.LocaleLabels;

import java.util.Date;
import java.util.Locale;

/**
 * Formatterer en dato med SimpleDateFormat pattern til gjeldende språk.
 *
 */
public class DateUtil {
    private final static long minute = 1000*60;
    private final static long hour = minute*60;
    private final static long day = hour*24;


    private DateUtil() {
    }

    /**
     * Formatterer mellom SimpleDateFormat pattern og "lesbar" tekst
     * Eks: dd.MM.yyy HH:mm -> dd.mm.åååå tt:mm
     *
     * @param dateFormat
     * @param locale
     * @return Formattert streng
     */
    public static String format(String dateFormat, Locale locale) {
        if (dateFormat == null) {
            return null;
        }
        char year = LocaleLabels.getLabel("aksess.dateformat.character.year", locale).charAt(0);
        char month = LocaleLabels.getLabel("aksess.dateformat.character.month", locale).charAt(0);
        char day = LocaleLabels.getLabel("aksess.dateformat.character.day", locale).charAt(0);
        char hour = LocaleLabels.getLabel("aksess.dateformat.character.hour", locale).charAt(0);
        char minute = LocaleLabels.getLabel("aksess.dateformat.character.minute", locale).charAt(0);
        char second = LocaleLabels.getLabel("aksess.dateformat.character.second", locale).charAt(0);

        StringBuilder formatted = new StringBuilder();
        for(int i=0; i < dateFormat.length(); i++ ) {
            char current = dateFormat.charAt(i);
            if (current == 'y') {
                formatted.append(year);
            } else if (current == 'M') {
                formatted.append(month);
            } else if (current == 'd') {
                formatted.append(day);
            } else if (current == 'H') {
                formatted.append(hour);
            } else if (current == 'm') {
                formatted.append(minute);
            }else if (current == 's') {
                formatted.append(second);
            } else {
                formatted.append(current);
            }
        }

        return formatted.toString();

    }


    public static String getAgeAsString(Date date, Locale locale) {
        Date now = new Date();

        long diff = now.getTime() - date.getTime();

        if (diff <= 0) {
            return LocaleLabels.getLabel("aksess.dateformat.now", locale);
        }

        if (diff < 59*minute) {
            long mins = (diff/minute);
            if (mins == 1) {
                return "1 "  + LocaleLabels.getLabel("aksess.dateformat.minute", locale);
            } else {
                return mins + " "  + LocaleLabels.getLabel("aksess.dateformat.minutes", locale);
            }

        }

        if (diff < 24*hour) {
            long hours = (diff/hour);
            if (hours == 1) {
                return "1 "  + LocaleLabels.getLabel("aksess.dateformat.hour", locale);
            } else {
                return hours + " "  + LocaleLabels.getLabel("aksess.dateformat.hours", locale);
            }
        }

        long days = (diff/day);
        if (days == 1) {
            return "1 "  + LocaleLabels.getLabel("aksess.dateformat.day", locale);
        } else {
            return days + " "  + LocaleLabels.getLabel("aksess.dateformat.days", locale);
        }
    }

}
