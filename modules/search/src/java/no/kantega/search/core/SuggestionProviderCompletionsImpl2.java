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
import no.kantega.search.index.Fields;
import no.kantega.search.index.IndexManager;
import no.kantega.search.query.SuggestionQuery;
import no.kantega.search.result.Suggestion;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.search.HitCollector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardTermEnum;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * SuggestionProvider som tilbyr forslag til autocompletions av ord. St�tter ogs� flere ord.
 * Denne implementasjonen gir ord basert p� hvilke termer som finnes ofte i samme dokument som den gitte frasen.
 * Basert p� forslag fra Eirik.
 *
 * Date: Jan 20, 2009
 * Time: 6:58:27 AM
 *
 * @author Tarje Killingberg
 */
public class SuggestionProviderCompletionsImpl2 implements SuggestionProvider {

    private static final String SOURCE = SuggestionProviderCompletionsImpl2.class.getName();

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

    /**
     * TODO: normaliser
     *
     * @param query
     * @return
     * @throws IOException
     */
    private List<Suggestion> doSuggest(SuggestionQuery query) throws IOException {
        List<Suggestion> suggestions = new ArrayList<Suggestion>();

        String[] terms = query.getText().trim().split(" ");
        String[] completedWords = new String[terms.length - 1];
        StringBuilder prePhraseBuilder = new StringBuilder();
        for (int i = 0; i < terms.length - 1; i++) {
            completedWords[i] = terms[i];
            prePhraseBuilder.append(terms[i]).append(" ");
        }
        String incompleteWord = terms[terms.length - 1];


        long begin = System.currentTimeMillis();
        // lage et array med booleans som sier om dokumentet inneholder de f�rste ordene
        IndexReader r = null;

        try {
            r = indexManager.getIndexReaderManager().getReader("aksess");
            final boolean[] bits = new boolean[r.maxDoc()];
            HitCollector collector = new HitCollector() {
                @Override
                public void collect(int doc, float score) {
                    bits[doc] = true;
                }
            };

            // TODO booleanQuery?
            for (String word : completedWords) {
                new IndexSearcher(r).search(new TermQuery(new Term(Fields.CONTENT_UNSTEMMED, word)), collector);
            }

            PriorityQueue<TermFreqMatch> q = new PriorityQueue<TermFreqMatch>(100, new Comparator<TermFreqMatch>() {
                public int compare(TermFreqMatch termFreqMatch, TermFreqMatch termFreqMatch1) {
                    return termFreqMatch1.getFreq() - termFreqMatch.getFreq();
                }
            });

            WildcardTermEnum termEnum = new WildcardTermEnum(r, new Term(Fields.CONTENT_UNSTEMMED, incompleteWord + "*"));

            final TermDocs docs = r.termDocs();

            for (; termEnum.term() != null && termEnum.term().field() == Fields.CONTENT_UNSTEMMED; termEnum.next()) {

                if (!contains(completedWords, termEnum.term().text())) {
                    docs.seek(termEnum.term());
                    int i = 0;
                    while (docs.next()) {
                        if (bits[docs.doc()]) {
                            i += docs.freq();
                        }
                    }
                    if (i > 0) {
                        q.offer(new TermFreqMatch(termEnum.term(), i));
                    }
                }
            }

            String prePhrase = prePhraseBuilder.toString();
            for (int i = 0; i < query.getMax() && q.size() > 0; i++) {
                TermFreqMatch f = q.poll();
                suggestions.add(new Suggestion(f.getTerm().text(), prePhrase + f.getTerm().text(), f.getFreq()));
            }
        } finally {
            if (r != null) {
                r.close();
            }
        }

        return suggestions;
    }

    private boolean contains(String[] strings, String word) {
        for (String s : strings) {
            if (word.equals(s)) {
                return true;
            }
        }
        return false;
    }

    public void setIndexManager(IndexManager indexManager) {
        this.indexManager = indexManager;
    }

    static class TermFreqMatch {
        private Term term;
        private int freq;

        TermFreqMatch(Term term, int freq) {
            this.term = term;
            this.freq = freq;
        }

        public Term getTerm() {
            return term;
        }

        public int getFreq() {
            return freq;
        }
    }

}
