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
import no.kantega.search.criteria.Criterion;
import no.kantega.search.index.IndexManager;
import no.kantega.search.query.SearchQuery;
import no.kantega.search.result.*;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.search.*;

import java.io.IOException;
import java.util.List;

/**
 * Date: Dec 5, 2008
 * Time: 8:46:48 AM
 *
 * @author Tarje Killingberg
 */
public class SearchHandlerDefaultImpl implements SearchHandler {

    private static final String SOURCE = SearchHandlerDefaultImpl.class.getName();

    private IndexManager indexManager;


    /**
     * {@inheritDoc}
     */
    public SearchResult handleSearch(SearchQuery searchQuery) {
        SearchResult searchResult = new SearchResultDefaultImpl();

        try {
            searchResult = doSearch(searchQuery);
        } catch (IOException e) {
            Log.error(SOURCE, e, "handleSearch", null);
            throw new RuntimeException(e);
        }

        return searchResult;
    }

    public void setIndexManager(IndexManager indexManager) {
        this.indexManager = indexManager;
    }

    protected IndexSearcher getIndexSearcher() throws IOException {
        return indexManager.getIndexSearcherManager().getSearcher("aksess");
    }

    private SearchResult doSearch(SearchQuery searchQuery) throws IOException {
        long start = System.currentTimeMillis();
        Query query = getQueryFromCriteria(searchQuery.getCriteria());
        CachingWrapperFilter filter = getFilter(searchQuery);
        IndexSearcher is = getIndexSearcher();
        Sort sort = searchQuery.getSort();
        if (sort == null) {
            sort = Sort.RELEVANCE;
        }
        TopDocs topDocs = is.search(query, filter, searchQuery.getMaxHits(), sort);
        SearchResultDefaultImpl searchResult = createSearchResult(topDocs, query, is);
        searchResult.setTime(System.currentTimeMillis() - start);
        Log.info(SOURCE, "Query: " + query, "doSearch", null);
        Log.info(SOURCE, "Filter: " + filter, "doSearch", null);
        return searchResult;
    }

    /**
     * Tolker en liste med Criterion-objekter, og oversetter disse til et org.apache.lucene.search.Query.
     *
     * @param criteria en liste med Criterion-objekter
     * @return et org.apache.lucene.search.Query-objekt
     */
    protected Query getQueryFromCriteria(List<Criterion> criteria) {
        BooleanQuery query = new BooleanQuery();
        for (Criterion criterion : criteria) {
            try {
                query.add(criterion.getQuery(), criterion.getOperator());
            } catch (BooleanQuery.TooManyClauses e) {
                break;
            }
        }
        return query;
    }


    /**
     * Gj�r det samme som getQueryFromCriteria(List<Criterion>), men returnerer null hvis lista er tom.
     *
     * @param criteria en liste med Criterion-objekter
     * @return et org.apache.lucene.search.Query-objekt, eller null hvis den gitte lista var tom
     */
    protected Query getFilterQueryFromCriteria(List<Criterion> criteria) {
        Query retVal = null;
        if (criteria.size() > 0) {
            BooleanQuery query = new BooleanQuery();
            for (Criterion criterion : criteria) {
                query.add(criterion.getQuery(), criterion.getOperator());
            }
            retVal = query;
        }
        return retVal;
    }

    private CachingWrapperFilter getFilter(SearchQuery searchQuery) {
        CachingWrapperFilter filter = null;
        Query filterQuery = getFilterQueryFromCriteria(searchQuery.getFilterCriteria());
        if (filterQuery != null) {
            filter = new CachingWrapperFilter(new QueryWrapperFilter(filterQuery));
        }
        return filter;
    }

    private SearchResultDefaultImpl createSearchResult(TopDocs topDocs, Query query, IndexSearcher indexSearcher) throws CorruptIndexException, IOException {
        SearchResultDefaultImpl searchResult = new SearchResultDefaultImpl();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            searchResult.addDocumentHit(createDocumentHit(scoreDoc, query, indexSearcher));
        }
        QueryInfo queryInfo = new QueryInfo();
        queryInfo.setQuery(query);
        queryInfo.setAnalyzer(indexManager.getAnalyzerFactory().createInstance());
        searchResult.setQueryInfo(queryInfo);
        searchResult.setNumberOfHits(topDocs.totalHits);
        return searchResult;
    }

    private DocumentHit createDocumentHit(ScoreDoc scoreDoc, Query query, IndexSearcher indexSearcher) throws CorruptIndexException, IOException {
        DocumentHitImpl documentHit = createDocumentHit(scoreDoc, indexSearcher);
        documentHit.setExplanation(indexSearcher.explain(query, documentHit.getDocumentId()));
        return documentHit;
    }

    private DocumentHitImpl createDocumentHit(ScoreDoc scoreDoc, IndexSearcher indexSearcher) throws CorruptIndexException, IOException {
        DocumentHitImpl documentHit = new DocumentHitImpl();
        documentHit.setDocumentId(scoreDoc.doc);
        documentHit.setDocument(indexSearcher.doc(scoreDoc.doc));
        documentHit.setScore(scoreDoc.score);
        return documentHit;
    }

}
