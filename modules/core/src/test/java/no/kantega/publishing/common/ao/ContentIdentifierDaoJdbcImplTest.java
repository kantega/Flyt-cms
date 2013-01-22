package no.kantega.publishing.common.ao;

import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.ContentIdentifierDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath*:spring/ContentIdentifierTestContext.xml")
public class ContentIdentifierDaoJdbcImplTest {

    @Autowired
    private ContentIdentifierDao dao;

    @Test
    public void shouldGetAliasBySiteIdAndAssociationId(){
        String alias = dao.getAliasBySiteIdAndAssociationId(1, 1);
        assertThat(alias, is("/alias/"));
    }

    @Test
    public void shouldReturnNullWhenRequestingNonExistingAliasBySiteIdAndAssociationId(){
        String alias = dao.getAliasBySiteIdAndAssociationId(12, 1);
        assertNull(alias);
    }

    @Test
    public void shouldGetContentIdentifierBySiteIdAndAlias(){
        ContentIdentifier cid = dao.getContentIdentifierBySiteIdAndAlias(1, "/alias/");
        assertThat(cid.getAssociationId(), is(1));
        assertThat(cid.getContentId(), is(1));
        assertThat(cid.getSiteId(), is(1));
    }

    @Test
    public void shouldReturnNullWhenGetContentIdentifierByNonExistingSiteId(){
        ContentIdentifier cid = dao.getContentIdentifierBySiteIdAndAlias(12, "/alias/");
        assertNull(cid);
    }

    @Test
    public void shouldGetContentIdentifierBySiteIdAndAliasWithoutTrailingSlash(){
        ContentIdentifier cid = dao.getContentIdentifierBySiteIdAndAlias(1, "/alias");
        assertThat(cid.getAssociationId(), is(1));
        assertThat(cid.getContentId(), is(1));
        assertThat(cid.getSiteId(), is(1));
    }

    @Test
    public void getAllCIdsWithAlias(){
        List<ContentIdentifier> cids = dao.getContentIdentifiersByAlias("/alias");
        assertThat(cids.size(), is(1));
        ContentIdentifier cid = cids.get(0);

        assertThat(cid.getAssociationId(), is(1));
        assertThat(cid.getContentId(), is(1));
        assertThat(cid.getSiteId(), is(1));
    }

    @Test
    public void returnEmptyListIfNoResults(){
        List<ContentIdentifier> cids = dao.getContentIdentifiersByAlias("/aliasthatcouldnotexist");
        assertThat(cids.size(), is(0));
    }
}
