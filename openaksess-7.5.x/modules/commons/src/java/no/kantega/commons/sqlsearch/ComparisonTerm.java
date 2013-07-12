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

import no.kantega.commons.sqlsearch.SearchTerm;

/**
 * This is an abstract class representing a SearchTerm that is used for
 * comparing to some value.
 *
 * @author Eirik Bjorsnos <bjorsnos@underdusken.no>
 * @version $Revision$, $Date$
 */
public abstract class ComparisonTerm extends SearchTerm {

    /** The <i>equality</i> operator*/
    public static final int EQ = 1;

    /** The <i>greater than or equal</i> operator*/
    public static final int GE = 2;

    /** The <i>greater than</i> operator*/
    public static final int GT = 3;

    /** The <i>less than or equal</i> operator*/
    public static final int LE = 4;

    /** The <i>less than</i> operator */
    public static final int LT = 5;

    /** The <i> not equal</i> operator*/
    public static final int NE = 6;

    /** The actual operator used in this comparison*/
    private int comparison;


    /**
     * Returns a string containing the SQL operator used in this comparison.
     * @return a string representing the SQL comparison operator
     */
    public String getOperator() {

        switch(comparison) {
        case EQ:
            return "=";
        case GE:
            return ">=";
        case GT:
            return ">";
        case LE:
            return "<=";
        case LT:
            return "<";
        case NE:
            return "!=";
        }
        return null;
    }

    /**
     * Sets the actual operator used in this comparison.
     * @param comparison the comparison operator to set
     */
    public void setComparison(int comparison) {
        this.comparison = comparison;
    }

    /**
     * Returns the actual operator used in this comparison.
     * @return the operator type used in this comparison
     */
    public int getComparison() {
        return comparison;
    }
}
