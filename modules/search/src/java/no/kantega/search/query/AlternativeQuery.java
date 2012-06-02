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

import no.kantega.search.core.SuggestionProvider;
import no.kantega.search.index.Fields;

/**
 * Date: Jan 20, 2009
 * Time: 12:04:36 PM
 *
 * @author Tarje Killingberg
 */
public class AlternativeQuery implements SuggestionQuery {

    private static final String SOURCE = AlternativeQuery.class.getName();

    private String field = Fields.CONTENT_UNSTEMMED;
    private String text;
    private int max = 5;
    private float accuracy = 0.75f;


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

    public float getAccuracy() {
        return accuracy;
    }

    /**
     * Setter en verdi for hvor lik forslagene må være den gitte teksten.
     * Må være en verdi mellom 0 og 1 (begge ekslusiv). Default er 0.75.
     * 
     * @param accuracy en verdi for hvor lik forslagene må være den gitte teksten
     */
    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    /**
     * {@inheritDoc}
     */
    public SuggestionProvider getSuggestionsProvider(IndexManager indexManager) {
        SuggestionProviderAlternativesImpl provider = new SuggestionProviderAlternativesImpl();
        provider.setIndexManager(indexManager);
        return provider;
    }

}
