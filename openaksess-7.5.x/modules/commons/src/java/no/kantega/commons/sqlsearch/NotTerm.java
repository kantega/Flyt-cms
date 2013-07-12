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

/**
 * This class implements the logical NEGATION operator on individual
 * SearchTerms.
 * @author Eirik Bjorsnos <bjorsnos@underdusken.no>
 * @version $Revision$, $Date$
 */
public class NotTerm extends CompoundTerm implements java.io.Serializable {

    /** An array of terms to negate */
    private SearchTerm[] terms;

    /**
     * Constructor that takes a SearchTerm to be negated.
     * @param term the SearchTerm to be negated
     */
    public NotTerm(SearchTerm term) {
        super(term);
    }

    /**
     * Returns this SearchTerm and it's child's part of the
     * SQL WHERE clause.
     * @return the WHERE clause
     */
    public String whereClause() {
        return "NOT (" + getTerms()[0].whereClause() + ")";
    }

}
