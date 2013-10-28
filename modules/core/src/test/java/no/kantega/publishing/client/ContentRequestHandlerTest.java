package no.kantega.publishing.client;

import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.ContentIdentifierDao;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.content.api.ContentAO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/controllerTestContext.xml")
@WebAppConfiguration
public class ContentRequestHandlerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private SiteCache siteCache;

    @Autowired
    private ContentAO contentAO;

    @Autowired
    private ContentIdentifierDao contentIdentifierDao;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        Site site = new Site();
        site.setId(1);
        when(siteCache.getSiteByHostname("localhost")).thenReturn(site);
        when(siteCache.getSiteById(isA(Integer.class))).thenReturn(site);

        final Content content = new Content();
        Association association = new Association();
        association.setSiteId(1);
        content.setAssociations(Collections.singletonList(association));
        when(contentAO.getContent(isA(ContentIdentifier.class), isA(Boolean.class))).thenReturn(content);
        when(contentIdentifierDao.getContentIdentifierBySiteIdAndAlias(isA(Integer.class), isA(String.class))).thenReturn(ContentIdentifier.fromAssociationId(1));
    }

    @Test
    public void shouldRespondToPrettyUrl() throws Exception {
        ResultActions perform = mockMvc.perform(get("/content/123/Tittel"));
        perform.andExpect(status().isOk());

    }

    @Test
    public void shouldRespondToSingleAlias() throws Exception {
        ResultActions perform = mockMvc.perform(get("/alias"));
        perform.andExpect(status().isOk());

    }

    @Test
    public void shouldRespondToDoubleAlias() throws Exception {
        ResultActions perform = mockMvc.perform(get("/alias/alias"));
        perform.andExpect(status().isOk());

    }

    @Test
    public void shouldRespondToSingleAliasTrailingSlash() throws Exception {
        ResultActions perform = mockMvc.perform(get("/alias/"));
        perform.andExpect(status().isOk());

    }

    @Test
    public void shouldRespondToDoubleAliasTrailingSlash() throws Exception {
        ResultActions perform = mockMvc.perform(get("/alias/alias/"));
        perform.andExpect(status().isOk());

    }
}
