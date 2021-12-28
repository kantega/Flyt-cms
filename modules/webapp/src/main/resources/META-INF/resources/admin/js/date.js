/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * This script expects the following properties to be set:
 * * date.labels.feilformat
 * * date.labels.skilletegn
 * * date.labels.feildag
 * * date.labels.feilmaned
 * * date.labels.feilar
 * * date.labels.feildagtall
 * * date.labels.feilmanedtall
 * * date.labels.feilartall
 * * date.labels.feilskuddarmaned
 * * date.labels.feiltidsformatKolon
 * * date.labels.feiltidsformat
 * * date.labels.feiltidsformatMinuttermindre
 * * date.labels.feiltidsformatMinutterstorre
 * * date.labels.feiltidsformatTimermindre
 * * date.labels.feiltidsformatTimerstorre
 */

openaksess.dateutils = {

    isDateNotEmpty: function (d) {
        if (d != "" && !isNaN(parseInt(d))) {
            return true;
        } else {
            return false;
        }
    },

    isTimeNotEmpty: function(d) {
        if (d != "" && !isNaN(parseInt(d))) {
            return true;
        } else {
            return false;
        }
    },

    checkDate: function (date) {
        var day, month, year;
        var startInx, endInx;
        var newDate = "";

        if (date.length < 8) {
            alert(properties.date.labels.feilformat);
        return -1;
        }

        if (date.indexOf('/', 0) != -1) {
            for (var i = 0; i < date.length; i++) {
                if (date.charAt(i) == "/") {
                    newDate += "."
                } else {
                    newDate += date.charAt(i);
                }
            }
            date = newDate;
        }

        startInx = 0;
        endInx = date.indexOf('.', 0);
        if (endInx == -1) {
            alert(properties.date.labels.skilletegn);
            return -1;
        }

        if ((date.charAt(startInx) == '0') && ((endInx - startInx) > 1)) {
            startInx++;
        }

        day = parseInt(date.substring(startInx, endInx));
        if (isNaN(day)) {
            alert(properties.date.labels.feildag);
            return -1;
        }

        startInx = endInx + 1;
        endInx = date.indexOf('.', startInx);
        if (endInx == -1) {
            alert(properties.date.labels.feilformat);
            return -1;
        }

        if ((date.charAt(startInx) == '0') && ((endInx - startInx) > 1)) {
            startInx++
        }

        month = parseInt(date.substring(startInx, endInx));
        if (isNaN(month)) {
            alert(properties.date.labels.feilmaned);
            return -1;
        } // month improperly specified

        year = parseInt(date.substring(endInx + 1, date.length));
        if (isNaN(year)) {
            alert(properties.date.labels.feilar);
            return -1;
        } // year improperly specified

        if ((day < 1) || (day > 31)) {
            alert(properties.date.labels.feildagtall);
            return -1;
        }

        if ((month < 1) || (month > 12)) {
            alert(properties.date.labels.feilmanedtall);
            return -1;
        }

        if (year < 1000 ) {
            alert(properties.date.labels.feilartall);
            return -1;
        }

        var chkDt = new Date(year, month - 1, day);
        var chkDays = chkDt.getDate();

        if (day != chkDays) {
            alert(properties.date.labels.feilskuddarmaned);
            return -1;
        }

        return 0;
    },

    checkTime: function(time) {
        var hours, min;
        var startInx, endInx;

        if (time.length < 3) {
            return -1;
        }

        startInx = 0;
        endInx = time.indexOf(':', 0);
        if (endInx == -1) {
            alert(properties.date.labels.feiltidsformatKolon);
            return -1;
        }

        if ((time.charAt(startInx) == '0') && ((endInx - startInx) > 1)) startInx++;

        hours = parseInt("" + time.substring(startInx, endInx));
        if (isNaN(hours)) {
            alert(properties.date.labels.feiltidsformat);
            return -1;
        }

        if ((time.charAt(endInx + 1) == '0') && ((endInx + 1 - time.length) > 1))
            startInx++;

        min = parseInt("" + time.substring(endInx + 1, time.length));
        if (isNaN(min)) {
            alert(properties.date.labels.feiltidsformat);
            return -1;
        }

        if (min < 0) {
            alert(properties.date.labels.feiltidsformatMinuttermindre);
            return -1;
        }

        if (min > 59) {
            alert(properties.date.labels.feiltidsformatMinutterstorre);
            return -1;
        }

        if (hours < 0) {
            alert(properties.date.labels.feiltidsformatTimermindre);
            return -1;
        }

        if (hours > 23) {
            alert(properties.date.labels.feiltidsformatTimerstorre);
            return -1;
        }

        return 0;
    }
};