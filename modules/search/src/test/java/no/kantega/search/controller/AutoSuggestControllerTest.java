package no.kantega.search.controller;

import no.kantega.search.core.Searcher;
import no.kantega.search.query.CompletionQuery;
import no.kantega.search.query.SuggestionQuery;
import no.kantega.search.result.Suggestion;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AutoSuggestControllerTest {
    

    @Test
    public void shouldSearchForSuggestions() throws Exception {
        Searcher searcher = mock(Searcher.class);
        AutoSuggestController controller = new AutoSuggestController();
        controller.setSearcher(searcher);
        String search = controller.search("query text", 8);

        ArgumentCaptor<SuggestionQuery> captor = ArgumentCaptor.forClass(SuggestionQuery.class);
        verify(searcher).suggest(captor.capture());

        CompletionQuery query = (CompletionQuery)captor.getValue();
        assertEquals("ContentUnstemmed", query.getField());
        assertEquals(8, query.getMax());
        assertEquals("query text", query.getText());
    }

    @Test
    public void shouldReturnSuggestions() throws Exception {
        List<Suggestion> expectedSuggestions = new ArrayList<Suggestion>();
        expectedSuggestions.add(new Suggestion("trondheim", 240));
        expectedSuggestions.add(new Suggestion("trondheimsfjorden", 82));
        expectedSuggestions.add(new Suggestion("trondheimssolistene", 23));
        expectedSuggestions.add(new Suggestion("trondheimsveien", 45));

        Searcher searcher = mock(Searcher.class);
        when(searcher.suggest((SuggestionQuery)any())).thenReturn(expectedSuggestions);

        AutoSuggestController controller = new AutoSuggestController();
        controller.setSearcher(searcher);
        String suggestionText = controller.search("trond", 3);

        String expected = "trondheim\n" +
                "trondheimsfjorden\n" +
                "trondheimssolistene\n" +
                "trondheimsveien\n";
        assertEquals("Wrong suggestion text", expected,suggestionText);
    }

}
