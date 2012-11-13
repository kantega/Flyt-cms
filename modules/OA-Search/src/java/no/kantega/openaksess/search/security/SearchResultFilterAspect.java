package no.kantega.openaksess.search.security;

import com.google.common.base.Predicate;
import no.kantega.commons.log.Log;
import no.kantega.openaksess.search.searchlog.dao.SearchLogDao;
import no.kantega.publishing.common.data.BaseObject;
import no.kantega.publishing.common.data.enums.ObjectType;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.enums.Privilege;
import no.kantega.search.api.search.GroupResultResponse;
import no.kantega.search.api.search.SearchQuery;
import no.kantega.search.api.search.SearchResponse;
import no.kantega.search.api.search.SearchResult;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.common.collect.Collections2.filter;

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

    @PreDestroy
    public void shutdownExecutorService(){
        executorService.shutdown();
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

    private void filterHits(SearchResponse searchResponse, final SecuritySession session) {
        List<GroupResultResponse> groupResultResponses = new ArrayList<GroupResultResponse>(searchResponse.getGroupResultResponses().size());
        for (GroupResultResponse groupResultResponse : searchResponse.getGroupResultResponses()) {
            List<SearchResult> originalSearchResults = groupResultResponse.getSearchResults();
            List<SearchResult> filteredResult = new ArrayList<SearchResult>(filter(originalSearchResults, new Predicate<SearchResult>() {
                public boolean apply(@Nullable SearchResult documentHit) {
                    return session.isAuthorized(toBaseObject(documentHit), Privilege.VIEW_CONTENT);
                }
            }));

            groupResultResponses.add(new GroupResultResponse(groupResultResponse.getGroupValue(),
                    getNumFoundReducedByRemovedElements(groupResultResponse, originalSearchResults, filteredResult),
                    filteredResult));
        }
        searchResponse.setGroupResultResponses(groupResultResponses);
    }

    private int getNumFoundReducedByRemovedElements(GroupResultResponse groupResultResponse, List<SearchResult> originalSearchResults, List<SearchResult> filteredResult) {
        return groupResultResponse.getNumFound().intValue() - (originalSearchResults.size() - filteredResult.size());
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
