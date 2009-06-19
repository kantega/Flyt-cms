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

import no.kantega.commons.sqlsearch.resultlimit.ResultLimitorStrategy;

import java.util.Vector;

/**
 * This is an abstract class representing a search-term.
 * Search criteria are represented as a tree of seach-terms, forming a
 * parse-tree used to generate the SQL query that returns the matching
 * Articles.
 *
 * SearchTerms are represented by this class. This is an abstract class:
 * subclasses implement specific math methods.
 *
 * @author Eirik Bjorsnos <bjorsnos@underdusken.no>
 * @version $Revision$, $Date$
 * @deprecated This api is not SQL injection-safe, don't use it for new code
 */
public abstract class SearchTerm implements java.io.Serializable {

    private String orderBy;
    private int firstResult = -1;
    private int maxResults = -1;
    /**
     * Generates the WHERE part of the SQL query (without including the string
     * "WHERE").
     * @return the WHERE clause
     */
    public abstract String whereClause();

    /**
     * Returns the names of the tables involved in this search.
     * @return A String[] containing the involved tables. The array may contain
     * multiple instances of each table name.
     */
    public abstract String[] getTables();

    /**
     * Returns the names of tables that should be ignored*/
    public String[] getIgnoreTables() {
        return new String[0];
    }


    /**
     * Returns the SQL query for this SearchTerm
     * @param the part following SELECT (what to select)
     * @return the SQL query
     */
    public String getQuery(String selectString) {

        String query = "SELECT " + selectString + " FROM ";

        Vector uV = new Vector();
        Vector iV = new Vector();

        String[] tables = getTables();
        String[] ignoreTables = getIgnoreTables();

        for (int i = 0; i < ignoreTables.length; i++) {
            iV.add(ignoreTables[i]);
        }



        for (int i = 0; i < tables.length; i++) {
            if (!existsInVector(tables[i], uV) && !existsInVector(tables[i],iV)) {
                uV.addElement(tables[i]);
            }
        }


        for (int i = 0; i < uV.size(); i++) {
            query += uV.elementAt(i).toString();

            if (uV.size() > 1 && i < uV.size() - 1) {
                query += ", ";
            }
        }

        query += " WHERE ";
        query += whereClause();

        if(getOrderBy() != null && getOrderBy().length() > 0) {
            query += " ORDER BY " +getOrderBy();
        }

        return query;

    }
    /**
     * Returns the SQL query for this SearchTerm
     * @param the part following SELECT (what to select)
     * @return the SQL query
     */
    public String getQuery(String selectString, ResultLimitorStrategy limitStrategy) {

        String query = "SELECT " +limitStrategy.limitFirstInSelect(this) +" " + selectString + " FROM ";

        Vector uV = new Vector();
        Vector iV = new Vector();

        String[] tables = getTables();
        String[] ignoreTables = getIgnoreTables();

        for (int i = 0; i < ignoreTables.length; i++) {
            iV.add(ignoreTables[i]);
        }



        for (int i = 0; i < tables.length; i++) {
            if (!existsInVector(tables[i], uV) && !existsInVector(tables[i],iV)) {
                uV.addElement(tables[i]);
            }
        }


        for (int i = 0; i < uV.size(); i++) {
            query += uV.elementAt(i).toString();

            if (uV.size() > 1 && i < uV.size() - 1) {
                query += ", ";
            }
        }

        query += " WHERE ";
        query += limitStrategy.limitInTerm(this).whereClause();

        if(getOrderBy() != null && getOrderBy().length() > 0) {
            query += " ORDER BY " +getOrderBy();
        }
        query += limitStrategy.limitAfterOrder(this);

        return query;

    }

    /**
     * Searches a Vector of Strings and returns true if a given String is
     * found.
     * @param string the String to search for
     * @param vector the Vector to search in
     * @return true if the String is found, false otherwise
     */
    private boolean existsInVector(String string, Vector vector) {

        for (int i = 0; i < vector.size(); i++) {
            if (vector.elementAt(i).equals(string)) {
                return true;
            }
        }
        return false;
    }


    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setFirstResult(int firstResult) {
        this.firstResult = firstResult;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public int getFirstResult() {
        return firstResult;
    }

    public int getMaxResults() {
        return maxResults;
    }
}
