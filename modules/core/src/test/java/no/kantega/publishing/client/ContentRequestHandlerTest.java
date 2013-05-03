package no.kantega.publishing.client;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.ResultActions;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.server.setup.MockMvcBuilders.standaloneSetup;

public class ContentRequestHandlerTest {

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = standaloneSetup(new ContentRequestHandler()).build();
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
