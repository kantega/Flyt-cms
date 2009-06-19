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

package no.kantega.search.core;

import no.kantega.commons.log.Log;
import no.kantega.search.query.SearchQuery;
import no.kantega.search.query.SearchQueryExtendedImpl;
import no.kantega.search.query.hitcount.QueryEnumeration;
import no.kantega.search.query.hitcount.HitCountQuery;
import no.kantega.search.result.*;
import no.kantega.search.criteria.Criterion;
import no.kantega.search.criteria.LastModifiedCriterion;
import no.kantega.search.index.Fields;
import org.apache.lucene.search.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Date: Dec 5, 2008
 * Time: 8:46:58 AM
 *
 * @author Tarje Killingberg
 */
public class SearchHandlerExtendedImpl extends SearchHandlerDefaultImpl {

    private static final String SOURCE = SearchHandlerExtendedImpl.class.getName();


    /**
     * {@inheritDoc}
     */
    public SearchResult handleSearch(SearchQuery query) {
        SearchResult searchResult = super.handleSearch(query);
        SearchResultExtendedImpl extResult = new SearchResultExtendedImpl();

        if (query instanceof SearchQueryExtendedImpl) {
            try {
                extResult = doSearch((SearchQueryExtendedImpl)query);
            } catch (IOException e) {
                Log.error(SOURCE, e, "handleSearch", null);
                throw new RuntimeException(e);
            }
        }

        extResult.setDocumentHits(searchResult.getDocumentHits());
        extResult.setQueryInfo(searchResult.getQueryInfo());
        extResult.setNumberOfHits(searchResult.getNumberOfHits());
        extResult.setTime(searchResult.getTime());
        searchResult = extResult;
        return searchResult;
    }

    private SearchResultExtendedImpl doSearch(SearchQueryExtendedImpl extQuery) throws IOException {
        long start = System.currentTimeMillis();
        SearchResultExtendedImpl searchResult = new SearchResultExtendedImpl();
        Query filterQuery = getFilter(extQuery);
        Log.info(SOURCE, "FilterQuery: " + filterQuery, "doSearch", null);
        CachingWrapperFilter filter = new CachingWrapperFilter(new QueryWrapperFilter(filterQuery));
        List<HitCountQuery> hitCountQueries = extQuery.getHitCountQueries();

        for (HitCountQuery hitCountQuery : hitCountQueries) {
            QueryEnumeration queryEnumer = hitCountQuery.getQueryEnumeration(getIndexSearcher().getIndexReader());
            String field = hitCountQuery.getField();
            String fieldTranslated = translateField(extQuery.getTermTranslator(), field);
            int otherCount = 0;
            while (queryEnumer.next()) {
                long substart = System.currentTimeMillis();
                Query q = queryEnumer.query();
                String term = queryEnumer.term();
                // TODO: Fjern senere - Harald
                //
                boolean doCont = false;
                if (hitCountQuery.getField().equals(Fields.LAST_MODIFIED)) {
                    for (Criterion crit : extQuery.getFilterCriteria()) {
                        if (crit.getClass().equals(LastModifiedCriterion.class)) {
                            LastModifiedCriterion lc = (LastModifiedCriterion)crit;
                            String range = "[" + lc.getLastModifiedFrom() + " TO " + lc.getLastModifiedTo() + "]";
                            if (!range.equals(term)) {
                                doCont = true;
                                break;
                            }
                        }
                    }
                }
                if (doCont) {
                    continue;
                }
                //
                TopDocs topDocs = getIndexSearcher().search(q, filter, extQuery.getMaxHits());
                if (hitCountQuery.getTerms().length == 0 || Arrays.binarySearch(hitCountQuery.getTerms(), term) >= 0) {
                    String termTranslated = translateTerm(extQuery.getTermTranslator(), field, term);
                    searchResult.addHitCount(createHitCount(field, fieldTranslated, term, termTranslated, topDocs.totalHits, System.currentTimeMillis() - substart));
                } else {
                    otherCount += topDocs.totalHits;
                }
            }
            if (!hitCountQuery.isIgnoreOther()) {
                String otherTranslated = translateTerm(extQuery.getTermTranslator(), field, HitCount.FIELD_OTHER);
                searchResult.addHitCount(createHitCount(field, fieldTranslated, HitCount.FIELD_OTHER, otherTranslated, otherCount, 0));
            }
        }

        searchResult.setExtendedTime(System.currentTimeMillis() - start);
        return searchResult;
    }

    private HitCount createHitCount(String field, String fieldTranslated, String term, String termTranslated, int nofHits, long time) {
        HitCountImpl hitCount = new HitCountImpl();
        hitCount.setHitCount(nofHits);
        hitCount.setField(field);
        hitCount.setFieldTranslated(fieldTranslated);
        hitCount.setTerm(term);
        hitCount.setTermTranslated(termTranslated);
        hitCount.setTime(time);
        return hitCount;
    }

    private Query getFilter(SearchQueryExtendedImpl extQuery) {
        Query retVal = null;
        Query orgQuery = getQueryFromCriteria(extQuery.getCriteria());
        Query orgFilter = getFilterQueryFromCriteria(extQuery.getFilterCriteria());
        if (orgFilter != null) {
            BooleanQuery booleanQuery = new BooleanQuery();
            booleanQuery.add(orgQuery, BooleanClause.Occur.MUST);
            booleanQuery.add(orgFilter, BooleanClause.Occur.MUST);
            retVal = booleanQuery;
        } else {
            retVal = orgQuery;
        }
        return retVal;
    }

    private String translateTerm(TermTranslator termTranslator, String fieldname, String term) {
        String retVal = term;
        if (termTranslator != null) {
            retVal = termTranslator.fromTerm(fieldname, term);
        }
        return retVal;
    }

    private String translateField(TermTranslator termTranslator, String fieldname) {
        String retVal = fieldname;
        if (termTranslator != null) {
            retVal = termTranslator.fromField(fieldname);
        }
        return retVal;
    }

}
