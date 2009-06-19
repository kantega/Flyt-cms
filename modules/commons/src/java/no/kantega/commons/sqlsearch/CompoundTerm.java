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

package no.kantega.commons.sqlsearch;

import java.util.Vector;

/**
 * This is an abstract class representing a SearchTerm that is composed of two
 * or more SearchTerms.
 * @author Eirik Bjorsnos <bjorsnos@underdusken.no>
 *
 * @version $Revision$, $Date$
 */
public abstract class CompoundTerm extends SearchTerm
    implements java.io.Serializable {

    /** A vector containing the terms currently added */
    private Vector terms;


    /**
     * Default constructor.
     */
    public CompoundTerm() {
        terms = new Vector();
    }

    /**
     * Constructor that takes one SearchTerm. I know that a compound term
     * is not very compund if it has only one term, but you can add terms with
     * the addTerm() methor.
     * @param term the SearchTerm to start with
     */
    public CompoundTerm(SearchTerm term) {
        this();
        terms.addElement(term);
    }

    /**
     * Constructor that takes two SearchTerms.
     * @param one the first term
     * @param two the second term
     */
    public CompoundTerm(SearchTerm one, SearchTerm two) {
        this();
        terms.addElement(one);
        terms.addElement(two);
    }

    /**
     * Constructor that takes an array of SearchTerms.
     * @param terms an array of SearchTerms
     */
    public CompoundTerm(SearchTerm[] terms) {
        this();
        for (int i = 0; i < terms.length; i++) {
            this.terms.addElement(terms[i]);
        }
    }

    /**
     * Adds a SearchTerm to this compundterm.
     * @param term the term to add
     */
    public void addTerm(SearchTerm term) {
        terms.addElement(term);
    }

    /**
     * Returns this SearchTerm's sub-SearchTerms.
     * @return the terms that the compund term consists off
     */
    public SearchTerm[] getTerms() {
        SearchTerm[] termsArray = new SearchTerm[terms.size()];

        for (int i = 0; i < termsArray.length; i++) {
            termsArray[i] = (SearchTerm) terms.elementAt(i);
        }

        return termsArray;
    }

    /**
     * Returns a String[] of tables that this SearchTerm and it's subterms
     * involves. It is used to construct the FROM part of the SQL query.
     * @return the string array of table names used
     */
    public String[] getTables() {
        int tableCount = 0;

        Vector tables = new Vector();


        for (int i = 0; i < getTerms().length; i++) {
            for (int k = 0; k < getTerms()[i].getTables().length; k++) {
                tables.addElement(getTerms()[i].getTables()[k]);
            }
        }

        String[] ret = new String[tables.size()];

        for (int i = 0; i < ret.length; i++) {
            ret[i] = (String) tables.elementAt(i);
        }

        return ret;
    }


    /**
     * Returns a String[] of tables that subterms tell us to ignore
     * @return the string array of table names to be ignored
     */
    public String[] getIgnoreTables() {
        int tableCount = 0;

        Vector tables = new Vector();


        for (int i = 0; i < getTerms().length; i++) {
            for (int k = 0; k < getTerms()[i].getIgnoreTables().length; k++) {
                tables.addElement(getTerms()[i].getIgnoreTables()[k]);
            }
        }

        return (String[]) tables.toArray(new String[0]);
    }
}
