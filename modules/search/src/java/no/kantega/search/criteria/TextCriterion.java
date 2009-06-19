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

package no.kantega.search.criteria;

import no.kantega.commons.log.Log;
import no.kantega.search.index.Fields;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;

/**
 * Criterion som søker etter ett eller flere ord i et felt.
 *
 * Date: Dec 5, 2008
 * Time: 11:06:35 AM
 *
 * @author Tarje Killingberg
 */
public class TextCriterion extends AbstractCriterion {

    public final static String DEFAULT_FIELDNAME = Fields.CONTENT;
    private static final String SOURCE = TextCriterion.class.getName();

    private Analyzer analyzer;
    private String fieldname;
    private String text;


    public TextCriterion(String text, Analyzer analyzer) {
        this(DEFAULT_FIELDNAME, text, analyzer);
    }

    public TextCriterion(String fieldname, String text, Analyzer analyzer) {
        this.fieldname = fieldname;
        this.text = text;
        this.analyzer = analyzer;
    }

    /**
     * {@inheritDoc}
     */
    public Query getQuery() {
        Query query = null;
        QueryParser queryParser = new QueryParser(fieldname, analyzer);
        queryParser.setDefaultOperator(QueryParser.AND_OPERATOR);
        try {
            query = queryParser.parse(text);

        } catch (ParseException e) {
            Log.error(SOURCE, e, "getQuery", null);
        }
        return query;
    }

}
