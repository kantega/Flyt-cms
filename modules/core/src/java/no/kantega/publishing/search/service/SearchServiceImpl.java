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

package no.kantega.publishing.search.service;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.SearchAO;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.enums.ContentStatus;
import no.kantega.publishing.common.data.enums.ContentVisibilityStatus;
import no.kantega.publishing.search.SearchField;
import no.kantega.publishing.search.model.AksessSearchHitContext;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.search.core.Searcher;
import no.kantega.search.criteria.ContentParentCriterion;
import no.kantega.search.criteria.ContentStatusCriterion;
import no.kantega.search.criteria.ContentTemplateCriterion;
import no.kantega.search.criteria.ContentTypeCriterion;
import no.kantega.search.criteria.Criterion;
import no.kantega.search.criteria.DocumentTypeCriterion;
import no.kantega.search.criteria.LanguageCriterion;
import no.kantega.search.criteria.LastModifiedCriterion;
import no.kantega.search.criteria.OrCriterion;
import no.kantega.search.criteria.PhraseCriterion;
import no.kantega.search.criteria.SiteCriterion;
import no.kantega.search.criteria.TextCriterion;
import no.kantega.search.criteria.VisibilityStatusCriterion;
import no.kantega.search.index.Fields;
import no.kantega.search.index.IndexManager;
import no.kantega.search.index.provider.DocumentProvider;
import no.kantega.search.index.provider.DocumentProviderSelector;
import no.kantega.search.query.AlternativeQuery;
import no.kantega.search.query.SearchQuery;
import no.kantega.search.query.SearchQueryDefaultImpl;
import no.kantega.search.query.SearchQueryExtendedImpl;
import no.kantega.search.query.hitcount.HitCountQuery;
import no.kantega.search.result.Alternative;
import no.kantega.search.result.DocumentHit;
import no.kantega.search.result.HitCount;
import no.kantega.search.result.SearchHit;
import no.kantega.search.result.SearchResult;
import no.kantega.search.result.SearchResultExtendedImpl;
import no.kantega.search.result.Suggestion;
import no.kantega.search.result.TermTranslator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.BooleanClause;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Date: Jan 28, 2009
 * Time: 8:46:24 AM
 *
 * @author Tarje Killingberg
 */
public class SearchServiceImpl implements SearchService {

    private static final String SOURCE = SearchServiceImpl.class.getName();

    private final String source = "SearchServiceImpl";
    private Searcher searcher;
    private TermTranslator termTranslator;
    private IndexManager indexManager;

    private int minNumberOfHits = 10;
    private float minScore = 0.75f;

    private SiteCache siteCache;


    /**
     * Utfører et søk mot den underliggende Searcher-implementasjonen
     *
     * @return en Map med ett SearchResultWrapper-objekt - med "searchResult" som nøkkel.
     * {@inheritDoc}
     */
    public SearchServiceResultImpl search(SearchServiceQuery searchServiceQuery) {
        SearchResult searchResult = new SearchResultExtendedImpl();
        List<Alternative> alternatives = new ArrayList<Alternative>();

        if (searchServiceQuery != null && searchServiceQuery.getSearchPhrase() != null) {
            // Bare søk hvis det er gitt en søkestreng
            SearchQuery searchQuery = createSearchQuery(searchServiceQuery);
            try{
                searchResult = searcher.search(searchQuery);
            } catch (NullPointerException e){
                Log.error(SOURCE, "invalid query", null, null);
                return null;
            }
            // Registrer søk med antall treff
            logSearch(searchServiceQuery, searchQuery, searchResult.getNumberOfHits());

            if (!isSufficient(searchResult)) {
                alternatives = suggestAlternatives(searchServiceQuery);
            }
        }

        SearchServiceResultImpl aksessSearchResult = createAksessSearchResult(searchServiceQuery, searchResult);
        aksessSearchResult.setAlternatives(alternatives);

        return aksessSearchResult;
    }

    public void setSearcher(Searcher searcher) {
        this.searcher = searcher;
    }

    public void setTermTranslator(TermTranslator termTranslator) {
        this.termTranslator = termTranslator;
    }

    public void setIndexManager(IndexManager indexManager) {
        this.indexManager = indexManager;
    }

    private SearchQuery createSearchQuery(SearchServiceQuery searchServiceQuery) {
        SearchQuery searchQuery;
        if (searchServiceQuery.getHitCountQueries().size() > 0) {
            searchQuery = createExtendedSearchQuery(searchServiceQuery);
        } else {
            searchQuery = createDefaultSearchQuery(searchServiceQuery);
        }
        return searchQuery;
    }

    private SearchQuery createExtendedSearchQuery(SearchServiceQuery searchServiceQuery) {
        SearchQueryExtendedImpl searchQuery = new SearchQueryExtendedImpl();
        searchQuery.setTermTranslator(termTranslator);
        searchQuery.setMaxHits(searchServiceQuery.getToIndex());
        List<Criterion> criterionList = parseSearchParameters(searchServiceQuery);
        for (Criterion criterion : criterionList) {
            searchQuery.addCriterion(criterion);
        }
        List<Criterion> filterList = parseFilterParameters(searchServiceQuery);
        for (Criterion criterion : filterList) {
            searchQuery.addFilterCriterion(criterion);
        }
        for (Criterion criterion : getDefaultFilters()) {
            searchQuery.addFilterCriterion(criterion);
        }
        for (HitCountQuery hitCountQuery : searchServiceQuery.getHitCountQueries()) {
            searchQuery.addHitCountQuery(hitCountQuery);
        }
        return searchQuery;
    }

    private SearchQuery createDefaultSearchQuery(SearchServiceQuery searchServiceQuery) {
        SearchQueryDefaultImpl searchQuery = new SearchQueryDefaultImpl();
        searchQuery.setMaxHits(searchServiceQuery.getToIndex());
        List<Criterion> criterionList = parseSearchParameters(searchServiceQuery);
        for (Criterion criterion : criterionList) {
            searchQuery.addCriterion(criterion);
        }
        List<Criterion> filterList = parseFilterParameters(searchServiceQuery);
        for (Criterion criterion : filterList) {
            searchQuery.addFilterCriterion(criterion);
        }
        for (Criterion criterion : getDefaultFilters()) {
            searchQuery.addFilterCriterion(criterion);
        }
        return searchQuery;
    }

    private boolean isSufficient(SearchResult searchResult) {
        return searchResult.getNumberOfHits() >= minNumberOfHits
                && !(searchResult.getDocumentHits().size() == 0)
                && searchResult.getDocumentHits().get(0).getScore() >= minScore;
    }

    private List<Alternative> suggestAlternatives(SearchServiceQuery q) {
        List<Alternative> alternatives = new ArrayList<Alternative>();
        if (q.getSearchPhrase() != null) {
            AlternativeQuery query = new AlternativeQuery();
            query.setText(q.getSearchPhrase());
            query.setMax(1);
            List<Suggestion> suggestions = searcher.suggest(query);
            for (Suggestion suggestion : suggestions) {
                alternatives.add(new Alternative(suggestion, SearchServiceQuery.PARAM_SEARCH_PHRASE + "=" + suggestion.getTerm()));
            }
        }
        return alternatives;
    }

    
    /**
     * Returns default filters: only search visible published pages
     * @return - List of criterion
     */
    protected List<Criterion> getDefaultFilters() {
        List<Criterion> criterions = new ArrayList<Criterion>();

        // Bare søk i aktivt innhold
        VisibilityStatusCriterion visibilityStatusCriterion = new VisibilityStatusCriterion(ContentVisibilityStatus.ACTIVE);
        criterions.add(visibilityStatusCriterion);

        // Bare søk i publisert innhold
        ContentStatusCriterion contentStatusCriterion = new ContentStatusCriterion(ContentStatus.PUBLISHED);
        criterions.add(contentStatusCriterion);

        return criterions;
    }

    /**
     * Lager en liste med Criterion-objekter som bør benyttes som Query.
     *
     * @param query et SearchServiceQuery-objekt
     * @return en liste med Criterion-objekter som bør benyttes som Query
     */
    private List<Criterion> parseSearchParameters(SearchServiceQuery query) {
        List<Criterion> criterionList = new ArrayList<Criterion>();

        /*
         * Query (søkefrasen)
         */
        Analyzer analyzer = indexManager.getAnalyzerFactory().createInstance();

        if (query.getStringParam(SearchServiceQuery.PARAM_SEARCH_PHRASE) != null) {
            String queryPhrase = query.getStringParam(SearchServiceQuery.PARAM_SEARCH_PHRASE);
            OrCriterion c = new OrCriterion();
            c.add(new TextCriterion(Fields.CONTENT, queryPhrase, analyzer));
            c.add(new TextCriterion(Fields.TITLE, queryPhrase, analyzer));
            c.add(new TextCriterion(Fields.ALT_TITLE, queryPhrase, analyzer));
            c.add(new TextCriterion(Fields.ALIAS, queryPhrase, analyzer));
            c.add(new TextCriterion(Fields.TM_TOPICS, queryPhrase, analyzer));
            c.add(new TextCriterion(Fields.KEYWORDS, queryPhrase, analyzer));
            c.add(new PhraseCriterion(Fields.CONTENT_UNSTEMMED, queryPhrase));
            for (SearchField field : query.getCustomSearchFields()) {
                List<Criterion> criteria = field.getQueryCriteria(queryPhrase, analyzer);
                if (criteria != null) {
                    for (Criterion criterion : criteria) {
                        c.add(criterion);
                    }
                }
            }
            criterionList.add(c);
        }

        return criterionList;
    }

    /**
     * Lager en liste med Criterion-objekter som bør benyttes som Filter.
     *
     * @param query et SearchServiceQuery-objekt
     * @return en liste med Criterion-objekter som bør benyttes som Filter
     */
    private List<Criterion> parseFilterParameters(SearchServiceQuery query) {
        List<Criterion> criterionList = new ArrayList<Criterion>();

        /*
         * Dokumenttype (som definert i aksess)
         */
        if (query.getIntegerParam(SearchServiceQuery.PARAM_DOCUMENT_TYPE) != null) {
            Integer documentType = query.getIntegerParam(SearchServiceQuery.PARAM_DOCUMENT_TYPE);
            DocumentTypeCriterion c = new DocumentTypeCriterion(documentType);
            criterionList.add(c);
        }

        /*
         * Contenttemplate (innholdsmal)
         */
        if (query.getIntegerParam(SearchServiceQuery.PARAM_CONTENT_TEMPLATE) != null) {
            Integer template = query.getIntegerParam(SearchServiceQuery.PARAM_CONTENT_TEMPLATE);
            ContentTemplateCriterion c = new ContentTemplateCriterion(template);
            criterionList.add(c);
        }

        /*
         * DocType (som regel content eller attachment)
         */
        if (query.getStringParam(SearchServiceQuery.PARAM_DOCTYPE) != null) {
            String docType = query.getStringParam(SearchServiceQuery.PARAM_DOCTYPE);
            ContentTypeCriterion c = new ContentTypeCriterion(docType);
            criterionList.add(c);
        }

        /*
         * ContentParents (foreldreelement i meny)
         */
        if (query.getIntegerParam(SearchServiceQuery.PARAM_CONTENT_PARENT) != null) {
            Integer contentParent = query.getIntegerParam(SearchServiceQuery.PARAM_CONTENT_PARENT);
            ContentParentCriterion c = new ContentParentCriterion(contentParent);
            criterionList.add(c);
        }

        /**
         * Excluded ContentParents (unntatte foreldreelement i meny)
         */
        if (query.getIntegerParam(SearchServiceQuery.PARAM_EXCLUDED_CONTENT_PARENT) != null) {
            Integer contentParent = query.getIntegerParam(SearchServiceQuery.PARAM_EXCLUDED_CONTENT_PARENT);
            ContentParentCriterion c = new ContentParentCriterion(contentParent);
            c.setOperator(BooleanClause.Occur.MUST_NOT);
            criterionList.add(c);
        }


        /*
         * LastModified (sist endret)
         */
        if (query.getDateParamAsString(SearchServiceQuery.PARAM_LAST_MODIFIED_FROM) != null && query.getDateParamAsString(SearchServiceQuery.PARAM_LAST_MODIFIED_TO) != null) {
            String lastModifiedFrom = query.getDateParamAsString(SearchServiceQuery.PARAM_LAST_MODIFIED_FROM);
            String lastModifiedTo = query.getDateParamAsString(SearchServiceQuery.PARAM_LAST_MODIFIED_TO);
            LastModifiedCriterion c = new LastModifiedCriterion(lastModifiedFrom, lastModifiedTo);
            criterionList.add(c);
        }

        /*
         * Site ID
         */
        if (query.getIntegerParam(SearchServiceQuery.PARAM_SITE_ID) != null) {
            Integer siteId = query.getIntegerParam(SearchServiceQuery.PARAM_SITE_ID);
            SiteCriterion c = new SiteCriterion(siteId);
            criterionList.add(c);
        }

        /*
         * Language ID
         */
        if (query.getIntegerParam(SearchServiceQuery.PARAM_LANGUAGE) != null) {
            Integer languageId = query.getIntegerParam(SearchServiceQuery.PARAM_LANGUAGE);
            LanguageCriterion c = new LanguageCriterion(languageId);
            criterionList.add(c);
        }

        /*
         * Custom filter parameters
         */
        for (SearchField field : query.getCustomSearchFields()) {
            List<Criterion> criteria = field.getFilterCriteria(query);
            if (criteria != null) {
                for (Criterion criterion : criteria) {
                    criterionList.add(criterion);
                }
            }
        }

        return criterionList;
    }

    private SearchServiceResultImpl createAksessSearchResult(SearchServiceQuery query, SearchResult searchResult) {
        long start = System.currentTimeMillis();
        SearchServiceResultImpl aksessSearchResult = new SearchServiceResultImpl(searchResult);
        if (query.getSearchPhrase() != null) {
            processSearchHits(query, aksessSearchResult);
            processHitCounts(aksessSearchResult);
        }
        Log.info(SOURCE, (System.currentTimeMillis() - start) / 1000d + " sekunder", "createAksessSearchResult(SearchServiceQuery, SearchResultExtendedImpl)", null);
        return aksessSearchResult;
    }

    private void logSearch(SearchServiceQuery searchServiceQuery, SearchQuery searchQuery, int numberOfHits) {
        try {
            if (Aksess.isSearchLogEnabled() && searchServiceQuery.getSearchPhrase().length() > 0) {
                // Register number of hits for this query
                Log.info(SOURCE, "Kaller SearchAO.registerSearch(" + searchServiceQuery.getSearchPhrase() + ", " + searchQuery.toString() + ", " + findSiteId(searchServiceQuery.getRequest()) + ", " + numberOfHits + ");", "logSearch", null);
                SearchAO.registerSearch(searchServiceQuery.getSearchPhrase(), searchQuery.toString(), findSiteId(searchServiceQuery.getRequest()), numberOfHits);
            }
        } catch (Exception e) {
            Log.error(SOURCE, e, null, null);
        }
    }

    private void processSearchHits(SearchServiceQuery query, SearchServiceResultImpl searchResult) {
        List<SearchHit> searchHits = new ArrayList<SearchHit>();
        List<DocumentHit> documentHits = searchResult.getSearchResult().getDocumentHits();
        DocumentProviderSelector selector = indexManager.getDocumentProviderSelector();
        AksessSearchHitContext searchHitContext = createSearchHitContext(query, searchResult);

        int documentCount = searchResult.getSearchResult().getDocumentHits().size();
        int currentPage = query.getPage();

        if (currentPage * query.getHitsPerPage() >= documentCount) {
            currentPage = (documentCount - 1) / query.getHitsPerPage();
        }
        if (currentPage < 0) {
            currentPage = 0;
        }

        int fromIdx = currentPage * query.getHitsPerPage();
        int toIdx = fromIdx + query.getHitsPerPage() <= documentCount ? fromIdx + query.getHitsPerPage() : documentCount;
        searchResult.setCurrentPage(currentPage);
        searchResult.setFromIndex(fromIdx);
        searchResult.setToIndex(toIdx);

        for (int i = fromIdx ; i < toIdx && i < documentHits.size(); i++) {
            DocumentHit documentHit = documentHits.get(i);
            String docType = documentHit.getDocument().get(Fields.DOCTYPE);
            DocumentProvider provider = selector.selectByDocumentType(docType);
            SearchHit searchHit = provider.createSearchHit();
            try {
                provider.processSearchHit(searchHit, searchHitContext, documentHit.getDocument());
                searchHits.add(searchHit);
            } catch (NotAuthorizedException e) {
                Log.error(SOURCE, e, null, null);
            } catch (Exception e) {
                Log.error(SOURCE, "Caught exception while processing search hit for content id \"" + documentHit.getDocument().get(Fields.CONTENT_ID) + "\". Stack trace follows.", null, null);
                Log.error(SOURCE, e, null, null);
            }
        }
        searchResult.setSearchHits(searchHits);
    }

    private AksessSearchHitContext createSearchHitContext(SearchServiceQuery query, SearchServiceResultImpl aksessSearchResult) {
        SearchResult searchResult = aksessSearchResult.getSearchResult();
        HttpServletRequest request = query.getRequest();
        AksessSearchHitContext context = new AksessSearchHitContext();
        try {
            context.setSecuritySession(SecuritySession.getInstance(request));
        } catch (SystemException e) {
            Log.error(SOURCE, e, null, null);
        }
        context.setSiteId(findSiteId(request));
        context.setQueryInfo(searchResult.getQueryInfo());
        return context;
    }

    /**
     * Genererer en Map med mappinger fra feltnavn til en liste med HitCount-objekter for denne kategorien
     *
     * @param aksessSearchResult et AksessSearchResult-objekt
     */
    private void processHitCounts(SearchServiceResultImpl aksessSearchResult) {
        Map<String, List<HitCount>> hitCountsMap = new HashMap<String, List<HitCount>>();
        SearchResult sr = aksessSearchResult.getSearchResult();
        if (sr instanceof SearchResultExtendedImpl) {
            List<HitCount> hitCounts = ((SearchResultExtendedImpl)aksessSearchResult.getSearchResult()).getHitCounts();
            for (HitCount hitCount : hitCounts) {
                String fieldTranslated = hitCount.getFieldTranslated();
                if (hitCountsMap.get(fieldTranslated) == null) {
                    hitCountsMap.put(fieldTranslated, new ArrayList<HitCount>());
                }
                if (hitCount.getHitCount() > 0) {
                    hitCountsMap.get(fieldTranslated).add(hitCount);
                }
            }
        }
        aksessSearchResult.setHitCounts(hitCountsMap);
    }

    private int findSiteId(HttpServletRequest request) {
        int retVal = -1;

        Content content = (Content)request.getAttribute("aksess_this");
        if (content != null) {
            retVal = content.getAssociation().getSiteId();
        }

        if (retVal == -1) {
            try {
                Site site = siteCache.getSiteByHostname(request.getServerName());
                if (site != null) {
                    retVal = site.getId();
                }
            } catch (SystemException e) {
                Log.error(SOURCE, e, null, null);
            }
        }
        return retVal;
    }


    public void setSiteCache(SiteCache siteCache) {
        this.siteCache = siteCache;
    }
}
