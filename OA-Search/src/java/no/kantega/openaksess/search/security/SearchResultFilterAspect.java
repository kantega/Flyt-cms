package no.kantega.openaksess.search.security;

import no.kantega.search.api.search.SearchResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SearchResultFilterAspect {



    @Around("execution(* no.kantega.search.api.search.Searcher.search(..))")
    public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
        SearchResponse searchResponse = (SearchResponse) pjp.proceed();



        return searchResponse;
    }
}
