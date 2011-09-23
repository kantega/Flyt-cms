package no.kantega.search.controller;

import no.kantega.search.core.Searcher;
import no.kantega.search.query.CompletionQuery;
import no.kantega.search.query.SuggestionQuery;
import no.kantega.search.result.Suggestion;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AutoSuggestControllerTest {

    @Mock
    Searcher searcher;
    private AutoSuggestController controller;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(searcher.suggest((SuggestionQuery)any())).thenReturn(createSuggestions());
        controller = new AutoSuggestController();
        controller.setSearcher(searcher);
    }

    @Test
    public void shouldSearchForSuggestions() throws Exception {
        controller.search("query text", 8);
        ArgumentCaptor<SuggestionQuery> captor = ArgumentCaptor.forClass(SuggestionQuery.class);
        verify(searcher).suggest(captor.capture());
        CompletionQuery query = (CompletionQuery)captor.getValue();
        assertEquals("ContentUnstemmed", query.getField());
        assertEquals(8, query.getMax());
        assertEquals("query text", query.getText());
    }

    @Test
    public void shouldReturnSuggestions() throws Exception {
        String suggestionText = controller.search("trond", 3);
        String expected = "trondheim\n" +
                "trondheimsfjorden\n" +
                "trondheimssolistene\n" +
                "trondheimsveien\n";
        assertEquals("Wrong suggestion text", expected,suggestionText);
    }

    @Test
    public void shouldReturnSuggestionsWhenQueryContainSpaces() throws Exception {
        String suggestionText = controller.search("noe trond", 3);
        String expected = "trondheim\n" +
                "trondheimsfjorden\n" +
                "trondheimssolistene\n" +
                "trondheimsveien\n";
        assertEquals("Wrong suggestion text", expected,suggestionText);
    }

    @Test
    public void shouldReturnSuggestionsWhenQueryContainUrlEncodedSpaces() throws Exception {
        String term  = URLEncoder.encode("noe trond", "UTF-8");
        String suggestionText = controller.search(term, 3);
        String expected = "trondheim\n" +
                "trondheimsfjorden\n" +
                "trondheimssolistene\n" +
                "trondheimsveien\n";
        assertEquals("Wrong suggestion text", expected,suggestionText);
    }
    private List<Suggestion> createSuggestions() {
        List<Suggestion> expectedSuggestions = new ArrayList<Suggestion>();
        expectedSuggestions.add(new Suggestion("trondheim", 240));
        expectedSuggestions.add(new Suggestion("trondheimsfjorden", 82));
        expectedSuggestions.add(new Suggestion("trondheimssolistene", 23));
        expectedSuggestions.add(new Suggestion("trondheimsveien", 45));
        return expectedSuggestions;
    }


}
