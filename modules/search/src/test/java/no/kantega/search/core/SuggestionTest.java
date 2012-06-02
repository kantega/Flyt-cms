/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.search.core;

import no.kantega.search.AbstractSearchTestCase;
import no.kantega.search.index.Fields;
import no.kantega.search.query.AlternativeQuery;
import no.kantega.search.query.CompletionQuery;
import no.kantega.search.result.Suggestion;

import java.util.List;

/**
 * Date: Jan 12, 2009
 * Time: 12:28:02 PM
 *
 * @author Tarje Killingberg
 */
public class SuggestionTest extends AbstractSearchTestCase {

    private static final String SOURCE = SuggestionTest.class.getName();


    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testSuggestCompletions() {
        String text = "trondheimsf";
        int max = 4;
        CompletionQuery query = new CompletionQuery();
        query.setField(Fields.CONTENT_UNSTEMMED);
        query.setText(text);
        query.setMax(max);
        List<Suggestion> suggestions = getSearcher().suggest(query);
        assertEquals(max, suggestions.size());
        for (Suggestion suggestion : suggestions) {
            assertTrue(suggestion.getPhrase() + " does not start with " + text, suggestion.getPhrase().startsWith(text));
        }

        text = "trondheimsfj";
        max = 5;
        query = new CompletionQuery();
        query.setField(Fields.CONTENT_UNSTEMMED);
        query.setText(text);
        query.setMax(max);
        suggestions = getSearcher().suggest(query);
        assertEquals(2, suggestions.size());
        for (Suggestion suggestion : suggestions) {
            assertTrue(suggestion.getPhrase() + " does not start with " + text, suggestion.getPhrase().startsWith(text));
        }

        text = "trondheim komm";
        max = 4;
        query = new CompletionQuery();
        query.setField(Fields.CONTENT_UNSTEMMED);
        query.setText(text);
        query.setMax(max);
        suggestions = getSearcher().suggest(query);
        assertEquals(max, suggestions.size());
        for (Suggestion suggestion : suggestions) {
            assertTrue(suggestion.getPhrase() + " does not start with " + text, suggestion.getPhrase().startsWith(text));
        }

        text = "trondheim by k";
        max = 5;
        query = new CompletionQuery();
        query.setField(Fields.CONTENT_UNSTEMMED);
        query.setText(text);
        query.setMax(max);
        suggestions = getSearcher().suggest(query);
        assertEquals(max, suggestions.size());
        for (Suggestion suggestion : suggestions) {
            assertTrue(suggestion.getPhrase() + " does not start with " + text, suggestion.getPhrase().startsWith(text));
        }
    }

    public void testSuggestAlternatives() {
        String text = "trondhiem";
        int max = 3;
        AlternativeQuery query = new AlternativeQuery();
        query.setField(Fields.CONTENT_UNSTEMMED);
        query.setText(text);
        query.setMax(max);

        List<Suggestion> suggestions = getSearcher().suggest(query);
        assertEquals(2, suggestions.size());
        assertEquals("trondheim", suggestions.get(0).getTerm());
    }

}
