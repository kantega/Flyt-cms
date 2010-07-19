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

package no.kantega.publishing.search;

import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.search.service.SearchServiceQuery;
import no.kantega.search.criteria.Criterion;
import no.kantega.search.query.hitcount.HitCountQuery;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 * @author Tarje Killingberg
 */
public interface SearchField {


    /**
     * Gets the fieldname of this SearchField.
     *
     * @return the fieldname of this SearchField.
     */
    public String getFieldname();

    /**
     * Adds custom fields to the search index. See
     * {@link #getQueryCriteria(String, Analyzer)} and
     * {@link #getHitCountQueries(SearchServiceQuery, HttpServletRequest, Content)}
     * for useful hints about indexing a field.
     *
     * @param content a content.
     * @param document the document to add custom fields to.
     */
    public void addToIndex(Content content, Document document);

    /**
     * Generates a list of criteria that should be considered when searching.
     * For example:
     * <pre>
     * List<Criterion> criteria = new ArrayList<Criterion>();
     * criteria.add(new TextCriterion(getFieldname(), queryPhrase, analyzer));
     * return criteria;
     * </pre>
     *
     * Note that, in most cases, for a field to be useful for searching the
     * field should be tokenized, but does not need to be stored, ie.:
     * <pre>
     * document.add(new Field(getFieldname(), "myValue", Field.Store.NO, Field.Index.ANALYZED));
     * </pre> 

     *
     * @param queryPhrase the query string that should be searched for.
     * @param analyzer the analyzer currently employed.
     * @return a list of criteria, or null if this is not relevant.
     */
    public List<Criterion> getQueryCriteria(String queryPhrase, Analyzer analyzer);

    /**
     * Generates a list of criteria that should be employed as filters when
     * searching. Whereas getQueryCriteria generates a list of criteria that
     * <em>should be considered</em> while searching, filters are hard
     * requirements - if they are not satisfied, then it is not a hit. Filters
     * are typically used during drilldown on a selected term.
     * For example:
     * <pre>
     * List<Criterion> criteria = new ArrayList<Criterion>();
     * String text = query.getStringParam(getFieldname());
     * criteria.add(new ExactCriterion(getFieldname(), text));
     * </pre>
     *
     * @param query the current SearchServiceQuery
     * @return a list of criteria, or null if this is not relevant.
     */
    public List<Criterion> getFilterCriteria(SearchServiceQuery query);

    /**
     * Generates a list of HitCountQueries that should be included in the
     * search. For example:
     * <pre>
     * List<HitCountQuery> hitCountQueries = new ArrayList<HitCountQuery>();
     * hitCountQueries.add(new HitCountQueryDefaultImpl(getFieldname()));
     * return hitCountQueries;
     * </pre>
     *
     * Note that for a hit count query for a field to be meaningful the field
     * should be stored but not tokenized, ie.:
     * <pre>
     * document.add(new Field(getFieldname(), "myValue", Field.Store.YES, Field.Index.NOT_ANALYZED));
     * </pre> 
     *
     * @param query the current SearchServiceQuery.
     * @param request the current HttpServletRequest.
     * @param content the current Content.
     * @return a list of HitCountQueries, or null if this is not relevant.
     */
    public List<HitCountQuery> getHitCountQueries(SearchServiceQuery query, HttpServletRequest request, Content content);

}
