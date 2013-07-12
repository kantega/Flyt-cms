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

package no.kantega.publishing.modules.linkcheck.sqlsearch;

import no.kantega.commons.sqlsearch.dialect.SQLDialect;
import no.kantega.commons.sqlsearch.SearchTerm;
import no.kantega.publishing.spring.RootContext;

import java.util.Date;

/**
 * User: Anders Skar, Kantega AS
 * Date: May 6, 2009
 * Time: 3:20:34 PM
 */
public class NotCheckedSinceTerm extends SearchTerm {

    private Date date;

    public NotCheckedSinceTerm(Date date) {
        this.date = date;
    }

    public String whereClause() {
        SQLDialect dialect = (SQLDialect) RootContext.getInstance().getBeansOfType(SQLDialect.class).values().iterator().next();
        String dateString = dialect.getDateAsString(date);

        return "lastchecked is null or lastchecked < " + dateString;
    }

    public String[] getTables() {
        return new String[] {"link"};
    }


}

