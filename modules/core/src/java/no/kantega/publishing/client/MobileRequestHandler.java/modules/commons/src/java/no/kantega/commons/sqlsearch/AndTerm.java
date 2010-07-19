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

import no.kantega.commons.sqlsearch.CompoundTerm;
import no.kantega.commons.sqlsearch.SearchTerm;


/**
 * This class implements the logical AND operator on individual SearchTerms.
 * @author Eirik Bjorsnos <bjorsnos@underdusken.no>
 * @version $Revision$, $Date$
 */
public class AndTerm extends CompoundTerm implements java.io.Serializable {


    /**
     * Default costructor
     */
    public AndTerm() {
        super();
    }

    /**
     * Constructor that takes two SearchTerms.
     * @param one one SearchTerm
     * @param two and another SearchTerm
     */
    public AndTerm(SearchTerm one, SearchTerm two) {
        super(one, two);
    }

    /**
     * Constructor that takes an array of SearchTerms.
     * @param terms the array of SearchTerms
     */
    public AndTerm(SearchTerm[] terms) {
        super(terms);
    }

    /**
     * Constructor that takes a single SearchTerm.
     * @param term the SearchTerm
     */
    public AndTerm(SearchTerm term) {
        super(term);
    }

    /**
     * Returns this SearchTerm and all of it's childrens part of the
     * SQL WHERE clause.
     * @return the WHERE clause
     */
    public String whereClause() {
        StringBuffer sb = new StringBuffer();

        sb.append("(");

        for (int i = 0; i < getTerms().length; i++) {

            if (i > 0) {
                sb.append(" AND ");
            }
            sb.append(getTerms()[i].whereClause());
        }

        sb.append(")");

        return sb.toString();
    }


}

