package no.kantega.publishing.client;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.DisplayTemplate;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.service.factory.AksessServiceFactory;
import no.kantega.publishing.common.util.helper.RequestHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Marvin B. Lillehaug <marvin.lillehaug@kantega.no>
 */
public class MobileRequestHandlerTest {

    @InjectMocks private MobileRequestHandler mobilRequestHandler;
    @Mock private AksessServiceFactory serviceFactory;
    @Mock private ContentManagementService cms;
    @Mock private SiteCache siteCache;
    @Mock private RequestHelper requestHelper;
    MockHttpServletRequest request;
    MockHttpServletResponse response;

    @Before
    public void setUp() throws Exception {
        mobilRequestHandler = new MobileRequestHandler();
        MockitoAnnotations.initMocks(this);

        Site site = mock(Site.class);
        when(site.getAlias()).thenReturn("openaksess");
        when(siteCache.getSiteById(1)).thenReturn(site);

        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        when(serviceFactory.getContentManagementService(request)).thenReturn(cms);
    }

    @Test
    public void testContainsAksess_this() throws Exception {
        Model model = new ExtendedModelMap();
        Integer thisId = 1;
        Content content = new Content();
        content.setContentTemplateId(1);
        Association association = new Association();
        association.setSiteId(1);
        content.addAssociation(association);

        DisplayTemplate displayTemplate = new DisplayTemplate();
        displayTemplate.setMobileView("/openaksess/public/content.jsp");
        content.setDisplayTemplateId(1);
        when(cms.getDisplayTemplate(1)).thenReturn(displayTemplate);

        when(cms.getContent(any(ContentIdentifier.class))).thenReturn(content);

        String view = mobilRequestHandler.handle(thisId, request, response, model);
        assertEquals("aksess_this not present", content, model.asMap().get("aksess_this"));
        assertEquals("Wrong view", "/openaksess/public/content.jsp", view);
    }

    @Test
    public void testSiteVariableIsReplaced() throws Exception {
        Model model = new ExtendedModelMap();
        Integer thisId = 1;
        Content content = new Content();
        content.setContentTemplateId(1);
        Association association = new Association();
        association.setSiteId(1);
        content.addAssociation(association);

        DisplayTemplate displayTemplate = new DisplayTemplate();
        displayTemplate.setMobileView("/$SITE/public/content.jsp");
        content.setDisplayTemplateId(1);
        when(cms.getDisplayTemplate(1)).thenReturn(displayTemplate);

        when(cms.getContent(any(ContentIdentifier.class))).thenReturn(content);

        String view = mobilRequestHandler.handle(thisId, request, response, model);
        assertEquals("Wrong view", "/openaksess/public/content.jsp", view);
    }

    @Test
    public void testReturnNormalViewIfNoMobileViewSet() throws Exception {
        Model model = new ExtendedModelMap();
        Integer thisId = 1;
        Content content = new Content();
        content.setContentTemplateId(1);
        Association association = new Association();
        association.setSiteId(1);
        content.addAssociation(association);

        DisplayTemplate displayTemplate = new DisplayTemplate();
        displayTemplate.setView("/openaksess/public/content.jsp");
        content.setDisplayTemplateId(1);
        when(cms.getDisplayTemplate(1)).thenReturn(displayTemplate);

        when(cms.getContent(any(ContentIdentifier.class))).thenReturn(content);

        String view = mobilRequestHandler.handle(thisId, request, response, model);
        assertEquals("aksess_this not present", content, model.asMap().get("aksess_this"));
        assertEquals("Wrong view", "/openaksess/public/content.jsp", view);
    }
}
