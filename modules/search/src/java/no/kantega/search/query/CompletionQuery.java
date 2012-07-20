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

package no.kantega.search.query;

import no.kantega.search.index.Fields;

/**
 * Date: Jan 20, 2009
 * Time: 12:05:01 PM
 *
 * @author Tarje Killingberg
 */
public class CompletionQuery implements SuggestionQuery {

    private static final String SOURCE = CompletionQuery.class.getName();

    private String field = Fields.CONTENT_UNSTEMMED;
    private String text;
    private int max = 5;


    /**
     * {@inheritDoc}
     */
    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    /**
     * {@inheritDoc}
     */
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * {@inheritDoc}
     */
    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

}
