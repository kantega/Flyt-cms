package no.kantega.publishing.api.taglibs.util;


import no.kantega.commons.urlplaceholder.UrlPlaceholderResolver;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.jsp.JspException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath*:spring/testContext.xml")
public class GetUrlTagTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired // mock sitecache defined in TestConfiguration
    private SiteCache siteCache;

    private GetUrlTag getUrlTag;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;
    private MockHttpSession mockHttpSession;

    @Before
    public void initTag(){
        getUrlTag = new GetUrlTag();
        MockServletContext mockServletContext = new MockServletContext();
        request = new MockHttpServletRequest();
        mockHttpSession = new MockHttpSession();

        request.setSession(mockHttpSession);
        response = new MockHttpServletResponse();
        MockPageContext pageContext = new MockPageContext(mockServletContext, request, response);
        getUrlTag.setPageContext(pageContext);

        // TODO when the project is upgraded to Spring 3.2 we can annotate the test class with @WebAppConfiguration and get this injected.
        WebApplicationContext wac = mock(WebApplicationContext.class);
        when(wac.getBean(UrlPlaceholderResolver.class)).thenReturn(applicationContext.getBean(UrlPlaceholderResolver.class));

        mockServletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);

    }

    @Test
    public void shouldAddSlashIfNotPresent() throws JspException, UnsupportedEncodingException {
        getUrlTag.setUrl("someurl");
        getUrlTag.doStartTag();
        assertEquals("/someurl", response.getContentAsString());
    }

    @Test
    public void shouldAddServerUrl() throws JspException, UnsupportedEncodingException {
        getUrlTag.setUrl("/someurl");
        getUrlTag.setAddserver(true);
        getUrlTag.doStartTag();
        assertEquals("http://localhost/someurl", response.getContentAsString());
    }

    @Test
    public void shouldAddServerUrlAndSlash() throws JspException, UnsupportedEncodingException {
        getUrlTag.setUrl("someurl");
        getUrlTag.setAddserver(true);
        getUrlTag.doStartTag();
        assertEquals("http://localhost/someurl", response.getContentAsString());
    }

    @Test
    public void shouldAddQueryParams() throws JspException, UnsupportedEncodingException {
        getUrlTag.setUrl("someurl");
        getUrlTag.setAddserver(true);
        getUrlTag.setQueryparams("q=abc&p=cba");
        getUrlTag.doStartTag();
        assertEquals("http://localhost/someurl?q=abc&amp;p=cba", response.getContentAsString());
    }

    @Test
    public void shouldAddQueryParamsToExistingParams() throws JspException, UnsupportedEncodingException {
        getUrlTag.setUrl("someurl?a=cde");
        getUrlTag.setAddserver(true);
        getUrlTag.setQueryparams("q=abc&p=cba");
        getUrlTag.doStartTag();
        assertEquals("http://localhost/someurl?a=cde&amp;q=abc&amp;p=cba", response.getContentAsString());
    }

    @Test
    public void shouldAddSiteIdIfInAdminMode() throws UnsupportedEncodingException, JspException, ContentNotFoundException {
        getUrlTag.setUrl("/alias");

        createFakeContent();

        getUrlTag.doStartTag();
        assertEquals("/alias?siteId=1", response.getContentAsString());
    }

    @Test
    public void shouldAddSiteIdIfInAdminModeWhenNotStartingWithSlash() throws UnsupportedEncodingException, JspException, ContentNotFoundException {
        getUrlTag.setUrl("alias");

        createFakeContent();

        getUrlTag.doStartTag();
        assertEquals("/alias?siteId=1", response.getContentAsString());
    }

    private void createFakeContent() {
        ContentIdentifier contentIdentifier = new ContentIdentifier();
        contentIdentifier.setSiteId(1);

        mockHttpSession.setAttribute("adminMode", true);

        Content content = new Content();
        Association association = new Association();
        association.setSiteId(1);
        content.setAssociations(Collections.singletonList(association));
        request.setAttribute("aksess_this", content);

        Site site = new Site();
        site.setId(1);
        when(siteCache.getDefaultSite()).thenReturn(site);
    }
}
