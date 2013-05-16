package no.kantega.openaksess.search.security;

import com.google.common.base.Predicate;
import no.kantega.openaksess.search.searchlog.dao.SearchLogDao;
import no.kantega.publishing.common.data.BaseObject;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.enums.Privilege;
import no.kantega.search.api.retrieve.IndexableContentTypeToObjectTypeMapping;
import no.kantega.search.api.search.GroupResultResponse;
import no.kantega.search.api.search.SearchQuery;
import no.kantega.search.api.search.SearchResponse;
import no.kantega.search.api.search.SearchResult;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Collections2.filter;

@Aspect
@Component
public class SearchResultFilterAspect {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private SearchLogDao searchLogDao;

    @Autowired
    private TaskExecutor taskExecutor;

    private Map<String, Integer> indexedContentTypeToObjectTypeMapping = new HashMap<>();

    @Around("execution(* no.kantega.search.api.search.Searcher.search(..))")
    public Object doFilterSearchResults(ProceedingJoinPoint pjp) throws Throwable {
        log.debug("Filtering search results");
        SearchResponse searchResponse = (SearchResponse) pjp.proceed();
        AksessSearchContext searchContext = (AksessSearchContext) searchResponse.getQuery().getSearchContext();
        SecuritySession session = searchContext.getSecuritySession();

        filterHits(searchResponse, session);
        registerPerformedSearch(searchResponse, searchContext);
        return searchResponse;
    }

    private void registerPerformedSearch(final SearchResponse searchResponse, final AksessSearchContext searchContext) {
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                SearchQuery query = searchResponse.getQuery();
                searchLogDao.registerSearch(query.getOriginalQuery(), query.getFilterQueries(), searchContext.getSiteId(), searchResponse.getNumberOfHits());
            }
        });
    }

    private void filterHits(SearchResponse searchResponse, final SecuritySession session) {
        List<GroupResultResponse> groupResultResponses = new ArrayList<>(searchResponse.getGroupResultResponses().size());
        for (GroupResultResponse groupResultResponse : searchResponse.getGroupResultResponses()) {
            List<SearchResult> originalSearchResults = groupResultResponse.getSearchResults();
            List<SearchResult> filteredResult = new ArrayList<>(filter(originalSearchResults, new Predicate<SearchResult>() {
                public boolean apply(@Nullable SearchResult documentHit) {
                    Integer mappedType = indexedContentTypeToObjectTypeMapping.get(documentHit.getIndexedContentType());
                    return mappedType == null || session.isAuthorized(toBaseObject(documentHit, mappedType), Privilege.VIEW_CONTENT);
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

    private BaseObject toBaseObject(SearchResult documentHit, int objectType) {
        return new SearchBaseObject(documentHit, objectType);
    }

    @Autowired
    private void setIndexedContentTypeToObjectTypeMappings(List<IndexableContentTypeToObjectTypeMapping> indexedContentTypeToObjectTypeMappings){
        for (IndexableContentTypeToObjectTypeMapping entry : indexedContentTypeToObjectTypeMappings) {
            this.indexedContentTypeToObjectTypeMapping.put(entry.getIndexableContentType(), entry.getObjectType());
        }
    }

    private class SearchBaseObject extends BaseObject {

        private final int objectType;

        private SearchBaseObject(SearchResult searchResult, int objectType) {
            this.objectType = objectType;
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
