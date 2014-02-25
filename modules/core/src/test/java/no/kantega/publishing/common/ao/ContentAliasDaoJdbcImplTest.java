package no.kantega.publishing.common.ao;

import no.kantega.publishing.api.content.ContentAliasDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Set;

import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= "classpath*:spring/testContext.xml")
public class ContentAliasDaoJdbcImplTest {

    @Autowired
    private ContentAliasDao dao;

    @Test
    public void shouldReturnAllAliases(){
        Set<String> allAliases = dao.getAllAliases();
        assertThat("Should contain /alias/", allAliases, hasItem("/alias/"));
    }

    @Test
    public void shouldNotReturnDeletedAlias(){
        Set<String> allAliases = dao.getAllAliases();
        assertThat("Should not contain /deletedalias/", allAliases, not(hasItem("/deletedalias/")));
    }
}
