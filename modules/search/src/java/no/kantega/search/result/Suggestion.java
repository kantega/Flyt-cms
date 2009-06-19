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

/**
 * Date: Jan 12, 2009
 * Time: 6:50:54 AM
 *
 * @author Tarje Killingberg
 */
public class Suggestion implements Comparable<Suggestion> {

    private static final String SOURCE = Suggestion.class.getName();

    private String term;
    private String phrase;
    private int docFreq;


    public Suggestion(String term, int docFreq) {
        this(term, term, docFreq);
    }

    public Suggestion(String term, String phrase, int docFreq) {
        this.term = term;
        this.phrase = phrase;
        this.docFreq = docFreq;
    }

    /**
     * Returnerer termen (ordet) til dette Suggestion-objektet.
     * 
     * @return termen til dette Suggestion-objektet
     */
    public String getTerm() {
        return term;
    }

    /**
     * Returnerer hele frasen til dette Suggestion-objektet.
     *
     * @return hele frasen til dette Suggestion-objektet
     */
    public String getPhrase() {
        return phrase;
    }

    /**
     * Returnerer dokumentfrekvensen til termen dette Suggestion-objektet representerer. Dokumentfrekvensen til en term
     * er antall dokumenter som inneholder termen.
     *
     * @return dokumentfrekvensen til termen dette Suggestion-objektet representerer
     */
    public int getDocFreq() {
        return docFreq;
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(Suggestion suggestion) {
        return suggestion.getDocFreq() - this.getDocFreq();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "\"" + getTerm() + "\" - \"" + getPhrase() + "\": " + getDocFreq();
    }

}
