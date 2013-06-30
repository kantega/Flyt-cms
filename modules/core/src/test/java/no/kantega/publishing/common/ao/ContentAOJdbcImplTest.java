package no.kantega.publishing.common.ao;

import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.ContentStatus;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.enums.ContentVisibilityStatus;
import no.kantega.publishing.content.api.ContentAO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= "classpath*:spring/testContext.xml")
public class ContentAOJdbcImplTest {

    @Autowired
    private ContentAO contentAO;


    @Test
    public void shouldGetContent(){
        Content content = contentAO.getContent(ContentIdentifier.fromContentId(1), false);
        assertNotNull(content);
        assertEquals("/alias/", content.getAlias());
    }

    @Test
    public void shouldGetActiveVersionWhenNotAdminMode(){
        Content content = contentAO.getContent(ContentIdentifier.fromContentId(1), false);
        assertNotNull(content);
        assertEquals(2, content.getVersion());
        assertEquals(ContentStatus.PUBLISHED, content.getStatus());
        assertEquals(ContentVisibilityStatus.ACTIVE, content.getVisibilityStatus());
        assertEquals("Nyhetsarkiv", content.getTitle());
    }

    @Test
    public void shouldGetActiveVersionWhenAdminMode(){
        Content content = contentAO.getContent(ContentIdentifier.fromContentId(1), true);
        assertNotNull(content);
        assertEquals(3, content.getVersion());
        assertEquals(ContentStatus.PUBLISHED_WAITING, content.getStatus());
        assertEquals(ContentVisibilityStatus.ACTIVE, content.getVisibilityStatus());
        assertEquals("Nyhetsarkiv v2", content.getTitle());
    }

    @Test
    public void shouldNotGetRequestedVersionIfNotAdminMode(){
        ContentIdentifier cid = ContentIdentifier.fromContentId(1);
        cid.setVersion(1);
        Content content = contentAO.getContent(cid, false);
        assertNotNull(content);
        assertEquals(2, content.getVersion());
        assertEquals(ContentStatus.PUBLISHED, content.getStatus());
        assertEquals(ContentVisibilityStatus.ACTIVE, content.getVisibilityStatus());
        assertEquals("Nyhetsarkiv", content.getTitle());
    }

    @Test
    public void shoulGetRequestedVersionWhenAdminMode(){
        ContentIdentifier cid = ContentIdentifier.fromContentId(1);
        cid.setVersion(1);
        Content content = contentAO.getContent(cid, true);
        assertNotNull(content);
        assertEquals(1, content.getVersion());
        assertEquals(ContentStatus.ARCHIVED, content.getStatus());
        assertEquals(ContentVisibilityStatus.ACTIVE, content.getVisibilityStatus());
        assertEquals("Nyhetsarkiv first", content.getTitle());
    }
}
