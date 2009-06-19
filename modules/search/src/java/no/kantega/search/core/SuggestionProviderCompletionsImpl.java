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

import no.kantega.commons.log.Log;
import no.kantega.search.index.IndexManager;
import no.kantega.search.query.SuggestionQuery;
import no.kantega.search.result.Suggestion;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.WildcardTermEnum;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;

/**
 * SuggestionProvider som tilbyr forslag til autocompletions av ord. Behandler hvert ord for seg, og tar ikke
 * hensyn til sammensetninger av ord (fraser).
 *
 * Date: Jan 12, 2009
 * Time: 1:02:37 PM
 *
 * @author Tarje Killingberg
 */
public class SuggestionProviderCompletionsImpl implements SuggestionProvider {

    private static final String SOURCE = SuggestionProviderCompletionsImpl.class.getName();

    private IndexManager indexManager;


    /**
     * {@inheritDoc}
     */
    public List<Suggestion> provideSuggestions(SuggestionQuery query) {
        List<Suggestion> suggestions = new ArrayList<Suggestion>();

        try {
            suggestions = doSuggest(query);
        } catch (IOException e) {
            Log.error(SOURCE, e, "provideSuggestions", null);
            throw new RuntimeException(e);
        }

        return suggestions;
    }

    public void setIndexManager(IndexManager indexManager) {
        this.indexManager = indexManager;
    }

    private List<Suggestion> doSuggest(SuggestionQuery query) throws IOException {
        List<Suggestion> suggestions = new ArrayList<Suggestion>();
        WildcardTermEnum termEnum = null;
        String[] terms = query.getText().trim().split(" ");
        StringBuilder prePhraseBuilder = new StringBuilder();
        for (int i = 0; i < terms.length-1; i++) {
            prePhraseBuilder.append(terms[i]).append(" ");
        }
        String word = terms[terms.length-1];

        Term wildcardTerm = new Term(query.getField(), prepareText(word));
        try {
            termEnum = new WildcardTermEnum(indexManager.getIndexReaderManager().getReader("aksess"), wildcardTerm);
            buildSuggestionList(suggestions, termEnum, prePhraseBuilder.toString());
        } finally {
            ensureClosed(termEnum);
        }

        Collections.sort(suggestions);
        if (suggestions.size() > query.getMax()) {
            suggestions = suggestions.subList(0, query.getMax());
        }

        return suggestions;
    }

    private void buildSuggestionList(List<Suggestion> suggestions, WildcardTermEnum termEnum, String prePhrase) throws IOException {
        String[] stopwords = indexManager.getAnalyzerFactory().getStopwords();
        for ( ; termEnum.term() != null; termEnum.next()) {
            Term term = termEnum.term();
            if (Arrays.binarySearch(stopwords, term.text()) < 0) {
                suggestions.add(new Suggestion(term.text(), prePhrase + term.text(), termEnum.docFreq()));
            } else {
                Log.info(SOURCE, "Term \"" + term.text() + "\" is defined as stop-word. Ignoring.", "buildSuggestionList", null);
            }
        }
    }

    private void ensureClosed(WildcardTermEnum termEnum) {
        if (termEnum != null) {
            try {
                termEnum.close();
            } catch (IOException e) {
                Log.error(SOURCE, e, "suggest", null);
            }
        }
    }

    private String prepareText(String text) {
        if (!text.endsWith("*")) {
            text = text + "*";
        }
        return text;
    }

}
