package no.kantega.publishing.common.ao;

import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.ContentStatus;
import no.kantega.publishing.api.content.Language;
import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.data.attributes.HtmltextAttribute;
import no.kantega.publishing.common.data.attributes.TextAttribute;
import no.kantega.publishing.common.data.attributes.UrlAttribute;
import no.kantega.publishing.common.data.enums.AttributeDataType;
import no.kantega.publishing.common.data.enums.ContentType;
import no.kantega.publishing.common.data.enums.ContentVisibilityStatus;
import no.kantega.publishing.content.api.ContentAO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.*;

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

    @Test
    public void shouldGetNullIfContentDoesNotExist(){
        assertNull(contentAO.getContent(ContentIdentifier.fromAssociationId(123), true));
    }

    @Test
    public void shouldNotDeleteActiveVersionWhenNotToldSo(){
        ContentIdentifier cid = ContentIdentifier.fromContentId(1);
        cid.setLanguage(Language.NORWEGIAN_BO);
        cid.setVersion(2);
        assertNotNull(contentAO.getContent(cid, false));
        contentAO.deleteContentVersion(cid, false);
        assertNotNull(contentAO.getContent(cid, false));
    }

    @Test
    public void shouldDeleteActiveVersionWhenToldSo(){
        ContentIdentifier cid = ContentIdentifier.fromContentId(1);
        cid.setLanguage(Language.NORWEGIAN_BO);
        cid.setVersion(2);
        assertNotNull(contentAO.getContent(cid, false));
        contentAO.deleteContentVersion(cid, true);
        assertNotNull(contentAO.getContent(cid, true));
    }

    @Test
    public void shouldGetAllContentVersions(){
        ContentIdentifier cid = ContentIdentifier.fromContentId(1);
        cid.setLanguage(Language.NORWEGIAN_BO);
        List<Content> allContentVersions = contentAO.getAllContentVersions(cid);
        assertEquals(3, allContentVersions.size());

        assertEquals(3, allContentVersions.get(0).getVersion());
        assertEquals(2, allContentVersions.get(1).getVersion());
        assertEquals(1, allContentVersions.get(2).getVersion());
    }

    @Test
    public void shouldGetPublishedContentVersionTitle(){
        assertEquals("Nyhetsarkiv", contentAO.getTitleByAssociationId(1));
    }

    @Test
    public void shouldGetParentAssociationId(){
        ContentIdentifier parent = contentAO.getParent(ContentIdentifier.fromAssociationId(1));
        assertEquals(0, parent.getAssociationId());
    }



    @Test
    public void shouldCheckInNewContent(){
        Content c = new Content();
        c.setAlias("/new/");
        c.setContentTemplateId(1);
        c.setDisplayTemplateId(1);
        c.setDescription("Bra innhold!");
        c.setTitle("LALA");
        c.setType(ContentType.PAGE);
        c.setAttributes(Arrays.asList(new TextAttribute("text", "En kort liten tekst"),
                new HtmltextAttribute("htmltext", "<p>lalalla</p>"),
                new UrlAttribute("theinternet", "https://www.theinternetz.com")), AttributeDataType.CONTENT_DATA);
        Association association = new Association();
        association.setParentAssociationId(1);
        association.setCategory(new AssociationCategory(1));
        c.setAssociations(Arrays.asList(association));
        Attachment attachment = new Attachment();
        attachment.setFilename("Attachment.txt");
        attachment.setData(new byte[]{1, 2, 3, 4});
        attachment.setSize(4);
        c.addAttachment(attachment);

        Multimedia m = new Multimedia();
        m.setAuthor("author");
        m.setData(new byte[]{4, 3, 2, 1});
        m.setDescription("Cool pix");
        m.setFilename("pic.png");
        m.setName("PIC");
        m.setSize(4);
        c.addMultimedia(m);

        Content content = contentAO.checkInContent(c, ContentStatus.PUBLISHED);

        Content saved = contentAO.getContent(content.getContentIdentifier(), false);
        assertNotNull(saved);
        assertEquals(content.getTitle(), saved.getTitle());
        assertEquals(content.getAlias(), saved.getAlias());
        assertEquals(content.getContentTemplateId(), saved.getContentTemplateId());
        assertEquals(content.getDisplayTemplateId(), saved.getDisplayTemplateId());
        assertEquals(content.getDescription(), saved.getDescription());
        assertEquals(content.getType(), saved.getType());
        assertNotNull(saved.getAssociation().getAssociationId());
        assertEquals(content.getAssociation().getParentAssociationId(), saved.getAssociation().getParentAssociationId());


        List<Attachment> attachmentList = AttachmentAO.getAttachmentList(saved.getContentIdentifier());
        assertFalse(attachmentList.isEmpty());
        assertNotNull(attachmentList.get(0).getId());
        assertEquals(saved.getId(), attachmentList.get(0).getContentId());


    }
}
