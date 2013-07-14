package no.kantega.publishing.common;

import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.content.api.ContentIdHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= "classpath*:spring/testContext.xml")
public class ContentIdHelperImplTest {

    @Autowired
    private ContentIdHelper contentIdHelper;

    @Test
    public void shouldSetContentIdFromAssociationId(){
        ContentIdentifier cid = ContentIdentifier.fromAssociationId(1);
        assertEquals(1, cid.getAssociationId());
        assertEquals(-1, cid.getContentId());
        contentIdHelper.assureContentIdAndAssociationIdSet(cid);
        assertEquals(1, cid.getAssociationId());
        assertEquals(1, cid.getContentId());
    }

    @Test
    public void shouldSetAssociationIdFromContentId(){
        ContentIdentifier cid = ContentIdentifier.fromContentId(1);
        assertEquals(-1, cid.getAssociationId());
        assertEquals(1, cid.getContentId());
        contentIdHelper.assureContentIdAndAssociationIdSet(cid);
        assertEquals(1, cid.getAssociationId());
        assertEquals(1, cid.getContentId());
    }
}
