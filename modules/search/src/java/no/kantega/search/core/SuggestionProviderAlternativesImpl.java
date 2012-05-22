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
import no.kantega.search.query.AlternativeQuery;
import no.kantega.search.query.SuggestionQuery;
import no.kantega.search.result.Suggestion;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Date: Jan 15, 2009
 * Time: 1:20:13 PM
 *
 * @author Tarje Killingberg
 */
public class SuggestionProviderAlternativesImpl implements SuggestionProvider {

    private static final String SOURCE = SuggestionProviderAlternativesImpl.class.getName();

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

    private List<Suggestion> doSuggest(SuggestionQuery suggQuery) throws IOException {
        AlternativeQuery query = (AlternativeQuery)suggQuery;
        String field = query.getField();
        int max = query.getMax() > 0 ? query.getMax() : 1;
        int numSuggestions = max >= 10 ? max + 5 : 15;
        String text = query.getText();
        float accuracy = query.getAccuracy();
        String[] stopwords = indexManager.getAnalyzerFactory().getStopwords();

        IndexReader reader = indexManager.getIndexReaderManager().getReader("aksess");
        IndexReader spellReader = indexManager.getIndexReaderManager().getReader("spelling");
        Directory spellDirectory = spellReader.directory();
        SpellChecker spellChecker = new SpellChecker(spellDirectory);
        spellChecker.setAccuracy(accuracy);

        String[] suggestions = {};
        try {
            suggestions = spellChecker.suggestSimilar(text, numSuggestions, reader, field, true);
        } catch (BooleanQuery.TooManyClauses e) {
            Log.error(getClass().getName(), e);
        }
        List<Suggestion> suggestionsByDocFreq = new ArrayList<Suggestion>();
        int textTermDocFreq = reader.docFreq(new Term(field, text));
        Term factoryTerm = new Term(field);
        for (String s : suggestions) {
            if (Arrays.binarySearch(stopwords, s) < 0) {
                int currentDocFreq = reader.docFreq(factoryTerm.createTerm(s));
                if (currentDocFreq > textTermDocFreq) {
                    // Bare ta med ord som har høyere dokumentfrekvens enn det originale
                    suggestionsByDocFreq.add(new Suggestion(s, currentDocFreq));
                }
            }
        }
        Collections.sort(suggestionsByDocFreq);
        if (suggestionsByDocFreq.size() > max) {
            suggestionsByDocFreq = suggestionsByDocFreq.subList(0, max);
        }
        spellChecker.close();
        spellDirectory.close();
        spellReader.close();
        reader.close();
        return suggestionsByDocFreq;
    }

}
