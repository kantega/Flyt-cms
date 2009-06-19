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

package no.kantega.search.result;

import no.kantega.search.result.Suggestion;

/**
 * Date: Jan 28, 2009
 * Time: 8:59:13 AM
 *
 * @author Tarje Killingberg
 */
public class Alternative {

    private static final String SOURCE = Alternative.class.getName();

    private Suggestion suggestion;
    private String queryString;


    public Alternative(Suggestion suggestion, String queryString) {
        this.suggestion = suggestion;
        this.queryString = queryString;
    }

    public Suggestion getSuggestion() {
        return suggestion;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }
    
}
