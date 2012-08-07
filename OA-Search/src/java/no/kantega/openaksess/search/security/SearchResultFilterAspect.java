package no.kantega.openaksess.search.security;

import no.kantega.commons.log.Log;
import no.kantega.openaksess.search.searchlog.dao.SearchLogDao;
import no.kantega.publishing.common.data.BaseObject;
import no.kantega.publishing.common.data.enums.ObjectType;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.enums.Privilege;
import no.kantega.search.api.search.SearchQuery;
import no.kantega.search.api.search.SearchResponse;
import no.kantega.search.api.search.SearchResult;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Aspect
@Component
public class SearchResultFilterAspect {
    @Autowired
    private SearchLogDao searchLogDao;

    private ExecutorService executorService;

    @PostConstruct
    public void setupExecutorService(){
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    @Around("execution(* no.kantega.search.api.search.Searcher.search(..))")
    public Object doFilterSearchResults(ProceedingJoinPoint pjp) throws Throwable {
        Log.debug("SearchResultFilterAspect", "Filtering search results");
        SearchResponse searchResponse = (SearchResponse) pjp.proceed();
        AksessSearchContext searchContext = (AksessSearchContext) searchResponse.getQuery().getSearchContext();
        SecuritySession session = searchContext.getSecuritySession();

        filterHits(searchResponse, session);
        registerPerformedSearch(searchResponse, searchContext);
        return searchResponse;
    }

    private void registerPerformedSearch(final SearchResponse searchResponse, final AksessSearchContext searchContext) {
        executorService.execute(new Runnable() {
            public void run() {
                SearchQuery query = searchResponse.getQuery();
                searchLogDao.registerSearch(query.getOriginalQuery(), query.getFilterQueries(), searchContext.getSiteId(), searchResponse.getNumberOfHits());
            }
        });
    }

    private void filterHits(SearchResponse searchResponse, SecuritySession session) {
        List<SearchResult> filteredHits = new ArrayList<SearchResult>();
        for (SearchResult documentHit : searchResponse.getDocumentHits()) {
            if(session.isAuthorized(toBaseObject(documentHit), Privilege.VIEW_CONTENT)){
                filteredHits.add(documentHit);
            }
        }
        searchResponse.setDocumentHits(filteredHits);
    }

    private BaseObject toBaseObject(SearchResult documentHit) {
        return new SearchBaseObject(documentHit);
    }

    private class SearchBaseObject extends BaseObject {

        private final int objectType;

        private SearchBaseObject(SearchResult searchResult) {
            objectType = ObjectType.ASSOCIATION;
            setSecurityId(searchResult.getSecurityId());
        }

        @Override
        public int getObjectType() {
            return objectType;
        }

        @Override
        public String getName() {
            return "";
        }

        @Override
        public String getOwner() {
            return "";
        }

        @Override
        public String getOwnerPerson() {
            return "";
        }
    }
}
