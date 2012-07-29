package no.kantega.openaksess.search;

import no.kantega.openaksess.search.security.AksessSearchContext;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.realm.SecurityRealm;
import no.kantega.publishing.spring.RootContext;
import no.kantega.search.api.search.SearchQuery;
import no.kantega.search.api.search.SearchResponse;
import no.kantega.search.api.search.Searcher;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/META-INF/spring/applicationContext-oaSearch-test.xml"})
public class SearchResultFilterAspectTest {
    @Autowired
    private Searcher searcher;

    @BeforeClass
    public static void setup(){
        ApplicationContext mock = mock(ApplicationContext.class);
        RootContext.setInstance(mock);
        when(mock.getBean(anyString())).thenReturn(new SecurityRealm());
    }

    @Test
    public void searcherShouldBeInterceptedAndResultsShouldBeFiltered(){
        SecuritySession securitySession = SecuritySession.createNewUnauthenticatedInstance();

        SearchQuery as = new SearchQuery(new AksessSearchContext(securitySession), "as");
        as.setFullQuery("title_no:as");
        SearchResponse search = searcher.search(as);
        assertEquals("Searchresult was not empty", 0, search.getNumberOfHits());
    }
}
