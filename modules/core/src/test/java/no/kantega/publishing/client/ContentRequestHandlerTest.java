package no.kantega.publishing.client;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/controllerTestContext.xml")
@WebAppConfiguration
public class ContentRequestHandlerTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
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
