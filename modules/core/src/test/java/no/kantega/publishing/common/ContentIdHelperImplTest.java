package no.kantega.publishing.common;

import com.google.gdata.util.common.base.Pair;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.content.api.ContentIdHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= "classpath*:spring/testContext.xml")
public class ContentIdHelperImplTest {

    @Autowired
    private SiteCache siteCache;

    @Autowired
    private ContentIdHelper contentIdHelper;

    @Before
    public void setup(){
        Site defaultSite = new Site();
        defaultSite.setId(1);
        defaultSite.setAlias("/alias");
        when(siteCache.getDefaultSite()).thenReturn(defaultSite);
        when(siteCache.getSiteById(1)).thenReturn(defaultSite);
        MockServletContext servletContext = new MockServletContext();
        servletContext.setContextPath("/");

        ((ServletContextAware)contentIdHelper).setServletContext(servletContext);
    }

    @Test(expected = ContentNotFoundException.class)
    public void shouldThrowForImage() throws ContentNotFoundException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "");
        ContentIdentifier cid = contentIdHelper.fromRequestAndUrl(request, "/images/image.png");
    }

    @Test
    public void aliasShouldReturnCorrectContentIdentifier() throws ContentNotFoundException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        ContentIdentifier cid = contentIdHelper.fromRequestAndUrl(request, "/alias/");
        assertNotNull("ContentIdentifier was null", cid);
        assertEquals("ContentIdentifier did not have associationId 1", 1, cid.getAssociationId());

        cid = contentIdHelper.fromRequestAndUrl(request, "https://sub.domain.no/alias/");
        assertNotNull("ContentIdentifier was null", cid);
        assertEquals("ContentIdentifier did not have associationId 1", 1, cid.getAssociationId());
    }

    @Test
    public void contentIdAndThisIdShouldReturnCorrectContentIdentifier() throws ContentNotFoundException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        ContentIdentifier cid = contentIdHelper.fromRequestAndUrl(request, "/content.ap?thisId=1&contentId=1");
        assertNotNull("ContentIdentifier was null", cid);
        assertEquals("ContentIdentifier did not have associationId 1", 1, cid.getAssociationId());
        assertEquals("ContentIdentifier did not have associationId 1", 1, cid.getContentId());

        cid = contentIdHelper.fromRequestAndUrl(request, "https://sub.domain.no/content.ap?thisId=1&contentId=1");
        assertNotNull("ContentIdentifier was null", cid);
        assertEquals("ContentIdentifier did not have associationId 1", 1, cid.getAssociationId());
        assertEquals("ContentIdentifier did not have associationId 1", 1, cid.getContentId());
    }

    @Test
    public void contentIdThisIdLanguageAndVersionShouldReturnCorrectContentIdentifier() throws ContentNotFoundException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        ContentIdentifier cid = contentIdHelper.fromRequestAndUrl(request, "/content.ap?thisId=1&contentId=1&version=2&language=2");
        assertNotNull("ContentIdentifier was null", cid);
        assertEquals("ContentIdentifier did not have associationId 1", 1, cid.getAssociationId());
        assertEquals("ContentIdentifier did not have contentid 1", 1, cid.getContentId());
        assertEquals("ContentIdentifier did not have language", 2, cid.getLanguage());
        assertEquals("ContentIdentifier did not have version", 2, cid.getVersion());

        cid = contentIdHelper.fromRequestAndUrl(request, "https://sub.domain.no/content.ap?thisId=1&contentId=1&version=2&language=2");
        assertNotNull("ContentIdentifier was null", cid);
        assertEquals("ContentIdentifier did not have associationId 1", 1, cid.getAssociationId());
        assertEquals("ContentIdentifier did not have contentid 1", 1, cid.getContentId());
        assertEquals("ContentIdentifier did not have language", 2, cid.getLanguage());
        assertEquals("ContentIdentifier did not have version", 2, cid.getVersion());
    }

    @Test
    public void contentIdShouldReturnCorrectContentIdentifierWithThisId() throws ContentNotFoundException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        ContentIdentifier cid = contentIdHelper.fromRequestAndUrl(request, "/content.ap?contentId=1");
        assertNotNull("ContentIdentifier was null", cid);
        assertEquals("ContentIdentifier did not have associationId 1", 1, cid.getAssociationId());
        assertEquals("ContentIdentifier did not have associationId 1", 1, cid.getContentId());
    }

    @Test
    public void prettyUrlShouldReturnCorrectContentIdentifierWithThisId() throws ContentNotFoundException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        ContentIdentifier cid = contentIdHelper.fromRequestAndUrl(request, "https://sub.domain.no/content/1/Tittel");
        assertNotNull("ContentIdentifier was null", cid);
        assertEquals("ContentIdentifier did not have associationId 1", 1, cid.getAssociationId());
        assertEquals("ContentIdentifier did not have associationId 1", 1, cid.getContentId());

        cid = contentIdHelper.fromRequestAndUrl(request, "/content/1/Tittel");
        assertNotNull("ContentIdentifier was null", cid);
        assertEquals("ContentIdentifier did not have associationId 1", 1, cid.getAssociationId());
        assertEquals("ContentIdentifier did not have associationId 1", 1, cid.getContentId());
    }

    @Test
    public void baseReturnCorrectContentIdentifierWithThisId() throws ContentNotFoundException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        ContentIdentifier cid = contentIdHelper.fromRequestAndUrl(request, "https://sub.domain.no/");
        assertNotNull("ContentIdentifier was null", cid);
        assertEquals("ContentIdentifier did not have associationId 1", 1, cid.getAssociationId());
        assertEquals("ContentIdentifier did not have associationId 1", 1, cid.getContentId());

        cid = contentIdHelper.fromRequestAndUrl(request, "/");
        assertNotNull("ContentIdentifier was null", cid);
        assertEquals("ContentIdentifier did not have associationId 1", 1, cid.getAssociationId());
        assertEquals("ContentIdentifier did not have associationId 1", 1, cid.getContentId());
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
        site.setAlias("/alias");
        when(siteCache.getSiteByHostname(serverName)).thenReturn(site);
        assertEquals(new Pair<>(1, "/"), getSiteIdFromRequest.invoke(contentIdHelper, request, "/alias"));
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
        String alias = "/" + serverName;
        site.setAlias(alias);
        when(siteCache.getSiteByHostname(serverName)).thenReturn(site);
        assertEquals(new Pair<>(1, "/"), getSiteIdFromRequest.invoke(contentIdHelper, request, alias));
    }

    @Test
    public void getSiteIdFromRequestShouldReturnSiteIdAndAdjustedUrlFromAliasAndAdjustedUrl() throws InvocationTargetException, IllegalAccessException {
        Method getSiteIdFromRequest = ReflectionUtils.findMethod(ContentIdHelperImpl.class, "getSiteIdFromRequest", HttpServletRequest.class, String.class);
        getSiteIdFromRequest.setAccessible(true);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        String serverName = "ahostname";
        Site site = new Site();
        site.setId(1);
        site.setAlias("/" + serverName);
        when(siteCache.getSites()).thenReturn(singletonList(site));
        assertEquals(new Pair<>(1, "/"), getSiteIdFromRequest.invoke(contentIdHelper, request, "/" + serverName));
    }

    @Test
    public void getSiteIdFromRequestShouldReturnSiteIdAndAdjustedUrlFromAliasAndAdjustedUrlWhenMatchinServerName() throws InvocationTargetException, IllegalAccessException {
        Method getSiteIdFromRequest = ReflectionUtils.findMethod(ContentIdHelperImpl.class, "getSiteIdFromRequest", HttpServletRequest.class, String.class);
        getSiteIdFromRequest.setAccessible(true);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        String serverName = "ahostname";
        request.setServerName(serverName);
        Site site = new Site();
        site.setId(1);
        site.setAlias("/" + serverName);

        when(siteCache.getSiteByHostname(serverName)).thenReturn(site);
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

    @Test
    public void shouldNotCutUrlWhenPartialMatchOnSite() throws ContentNotFoundException {
        Site site = new Site();
        site.setAlias("/se/");
        site.setId(1);
        when(siteCache.getSites()).thenReturn(singletonList(site));

        ContentIdentifier contentIdentifier = contentIdHelper.fromUrl("/seko");
        assertThat(contentIdentifier.getAssociationId(), is(3));
        assertThat(contentIdentifier.getContentId(), is(3));
        assertThat(contentIdentifier.getSiteId(), is(1));
    }

    /**
     * Test that verifies that correct site is returned when getUrlAdjustedBySiteAlias is called with
     * non null site parameter and ajustedUrl.startsWith(siteAliasWithTrailingSlash) == false.
     */
    @Test
    public void shouldNotForgetToSetSiteId() throws ContentNotFoundException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        request.setServerName("servername");
        Site site = new Site();
        site.setId(2);
        when(siteCache.getSiteByHostname("servername")).thenReturn(site.setAlias("/snn/"));
        when(siteCache.getSiteById(site.getId())).thenReturn(site);

        ContentIdentifier contentIdentifier = contentIdHelper.fromRequestAndUrl(request, "/");
        assertThat(contentIdentifier.getAssociationId(), is(4));
        assertThat(contentIdentifier.getSiteId(), is(site.getId()));
    }
}
