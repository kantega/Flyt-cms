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

package no.kantega.commons.sqlsearch.dialect;

import no.kantega.commons.sqlsearch.resultlimit.ResultLimitorStrategy;
import no.kantega.commons.sqlsearch.resultlimit.MsSQLResultLimitorStrategy;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 *
 */
public class MsSQLDialect implements SQLDialect {
    public ResultLimitorStrategy getResultLimitorStrategy() {
        return new MsSQLResultLimitorStrategy();
    }

    public String getDateAsString(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return "'" + df.format(date) + "'";
    }

    public boolean searchIsCaseSensitive() {
        return false;
    }
}
