package no.kantega.publishing.common;

import com.google.gdata.util.common.base.Pair;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.content.api.ContentIdHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ReflectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= "classpath*:spring/testContext.xml")
public class ContentIdHelperImplTest {

    @Autowired
    private SiteCache siteCache;

    @Autowired
    private ContentIdHelper contentIdHelper;

    @Test
    public void shouldReturnNullForImage() throws ContentNotFoundException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/images/image.png");
        ContentIdentifier cid = contentIdHelper.fromRequestAndUrl(request, "/images/image.png");
        assertNull("ContentIdentifier for image was not null", cid);
    }

    @Test
    public void aliasShouldReturnCorrectContentIdentifier() throws ContentNotFoundException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        ContentIdentifier cid = contentIdHelper.fromRequestAndUrl(request, "/alias/");
        assertNotNull("ContentIdentifier for image was not null", cid);
        assertEquals("ContentIdentifier did not have associationId 1", 1, cid.getAssociationId());
    }

    @Test
    public void getSiteIdFromRequestShouldReturnSiteIdAndAdjustedUrlFromUrl() throws InvocationTargetException, IllegalAccessException {
        Method getSiteIdFromRequest = ReflectionUtils.findMethod(ContentIdHelperImpl.class, "getSiteIdFromRequest", HttpServletRequest.class, String.class);
        getSiteIdFromRequest.setAccessible(true);

        assertEquals(new Pair<>(1, "/?siteId=1"), getSiteIdFromRequest.invoke(contentIdHelper, new MockHttpServletRequest("GET", "/"), "/?siteId=1"));
    }

    @Test
    public void getSiteIdFromRequestShouldReturnSiteIdAndAdjustedUrlFromParameter() throws InvocationTargetException, IllegalAccessException {
        Method getSiteIdFromRequest = ReflectionUtils.findMethod(ContentIdHelperImpl.class, "getSiteIdFromRequest", HttpServletRequest.class, String.class);
        getSiteIdFromRequest.setAccessible(true);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        request.setParameter("siteId", "1");
        assertEquals(new Pair<>(1, "/"), getSiteIdFromRequest.invoke(contentIdHelper, request, "/"));
    }

    @Test
    public void getSiteIdFromRequestShouldReturnSiteIdAndAdjustedUrlFromContent() throws InvocationTargetException, IllegalAccessException {
        Method getSiteIdFromRequest = ReflectionUtils.findMethod(ContentIdHelperImpl.class, "getSiteIdFromRequest", HttpServletRequest.class, String.class);
        getSiteIdFromRequest.setAccessible(true);

        Content content = new Content();
        Association association = new Association();
        association.setSiteId(1);
        content.setAssociations(asList(association));
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        request.setAttribute("aksess_this", content);
        assertEquals(new Pair<>(1, "/"), getSiteIdFromRequest.invoke(contentIdHelper, request, "/"));
    }

    @Test
    public void getSiteIdFromRequestShouldReturnSiteIdAndAdjustedUrlFromHostname() throws InvocationTargetException, IllegalAccessException {
        Method getSiteIdFromRequest = ReflectionUtils.findMethod(ContentIdHelperImpl.class, "getSiteIdFromRequest", HttpServletRequest.class, String.class);
        getSiteIdFromRequest.setAccessible(true);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        String serverName = "ahostname";
        request.setServerName(serverName);
        Site site = new Site();
        site.setId(1);
        when(siteCache.getSiteByHostname(serverName)).thenReturn(site);
        assertEquals(new Pair<>(1, "/"), getSiteIdFromRequest.invoke(contentIdHelper, request, "/"));
    }

    @Test
    public void getSiteIdFromRequestShouldReturnSiteIdAndAdjustedUrlFromHostnameAndAdjustedUrl() throws InvocationTargetException, IllegalAccessException {
        Method getSiteIdFromRequest = ReflectionUtils.findMethod(ContentIdHelperImpl.class, "getSiteIdFromRequest", HttpServletRequest.class, String.class);
        getSiteIdFromRequest.setAccessible(true);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        String serverName = "ahostname";
        request.setServerName(serverName);
        Site site = new Site();
        site.setId(1);
        site.setAlias("/" + serverName);
        when(siteCache.getSiteByHostname(serverName)).thenReturn(site);
        assertEquals(new Pair<>(1, "/" + serverName), getSiteIdFromRequest.invoke(contentIdHelper, request, "/" + serverName));
    }

    @Test
    public void getSiteIdFromRequestShouldReturnSiteIdAndAdjustedUrlFromAliasAndAdjustedUrl() throws InvocationTargetException, IllegalAccessException {
        Method getSiteIdFromRequest = ReflectionUtils.findMethod(ContentIdHelperImpl.class, "getSiteIdFromRequest", HttpServletRequest.class, String.class);
        getSiteIdFromRequest.setAccessible(true);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        String serverName = "ahostname";
        request.setServerName(serverName);
        Site site = new Site();
        site.setId(1);
        site.setAlias("/" + serverName);
        when(siteCache.getSites()).thenReturn(singletonList(site));
        assertEquals(new Pair<>(1, "/"), getSiteIdFromRequest.invoke(contentIdHelper, request, "/" + serverName));
    }


    @Test
    public void getSiteIdFromRequestShouldReturnSiteIdAndAdjustedUrlFromDefaultSite() throws InvocationTargetException, IllegalAccessException {
        Method getSiteIdFromRequest = ReflectionUtils.findMethod(ContentIdHelperImpl.class, "getSiteIdFromRequest", HttpServletRequest.class, String.class);
        getSiteIdFromRequest.setAccessible(true);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        Site site = new Site();
        site.setId(1);
        when(siteCache.getDefaultSite()).thenReturn(site);
        assertEquals(new Pair<>(1, "/"), getSiteIdFromRequest.invoke(contentIdHelper, request, "/"));
    }

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
