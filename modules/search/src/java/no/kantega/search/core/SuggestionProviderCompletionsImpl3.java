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
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.*;

import java.io.IOException;
import java.util.*;

/**
 * SuggestionProvider som tilbyr forslag til autocompletions av ord. Støtter også flere ord.
 * Denne implementasjonen gir ord basert på hvilke termer som faktisk er mest populære etter den gitte frasen.
 * 
 * Date: Jan 20, 2009
 * Time: 8:03:58 AM
 *
 * @author Tarje Killingberg
 */
public class SuggestionProviderCompletionsImpl3 implements SuggestionProvider {

    private static final String SOURCE = SuggestionProviderCompletionsImpl3.class.getName();

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
        long begin = System.currentTimeMillis();
        List<Suggestion> suggestions = new ArrayList<Suggestion>();

        String[] terms = query.getText().trim().split(" ");
        Term[] preTerms = new Term[terms.length - 1];
        StringBuilder prePhraseBuilder = new StringBuilder();
        String word = terms[terms.length-1];
        for (int i = 0; i < preTerms.length; i++) {
            preTerms[i] = new Term(query.getField(), terms[i]);
            prePhraseBuilder.append(terms[i]).append(" ");
        }
        String prePhrase = prePhraseBuilder.toString();
        MultiPhraseQuery multiPhraseQuery = new MultiPhraseQuery();
        for (Term t : preTerms) {
            multiPhraseQuery.add(t);
        }

        IndexSearcher indexSearcher = indexManager.getIndexSearcherManager().getSearcher("aksess");
//        TermEnum termEnum = getTermEnum(indexSearcher.getIndexReader(), query.getField(), words[words.length-1]);
        TermEnum termEnum = getOrderedTermEnum(indexSearcher.getIndexReader(), query.getField(), word);
        CachingWrapperFilter filter = new CachingWrapperFilter(new QueryWrapperFilter(multiPhraseQuery));

        for (int count = 0 ; termEnum.term() != null && count < query.getMax()*2; termEnum.next(), count++) {

            MultiPhraseQuery q = new MultiPhraseQuery();
            for (Term t : preTerms) {
                q.add(t);
            }
            Term current = termEnum.term();
            q.add(current);
            TopDocs topDocs = indexSearcher.search(q, filter, 1);
            suggestions.add(new Suggestion(current.text(), prePhrase + current.text(), topDocs.totalHits));
        }

        Collections.sort(suggestions);
        if (suggestions.size() > query.getMax()) {
            suggestions = suggestions.subList(0, query.getMax());
        }
        
        System.out.println(System.currentTimeMillis() - begin + " millisecs");
        return suggestions;
    }

    private TermEnum getTermEnum(IndexReader reader, String field, String word) throws IOException {
        TermEnum termEnum;
        if (!"".equals(word.trim())) {
            termEnum = new WildcardTermEnum(reader, new Term(field, word + "*"));
        } else {
            termEnum = reader.terms(new Term(field, ""));
        }
        return termEnum;
    }

    private TermEnum getOrderedTermEnum(IndexReader reader, String field, String word) throws IOException {
        TermEnum t = getTermEnum(reader, field, word);
        return new OrderedTermEnum(reader, t);

    }

    private class OrderedTermEnum extends TermEnum {

        private PriorityQueue<Term> queue;
        private IndexReader reader;


        public OrderedTermEnum(final IndexReader reader, TermEnum termEnum) throws IOException {
            this.reader = reader;
            queue = new PriorityQueue<Term>(10, new Comparator<Term>(){
                public int compare(Term term, Term term1) {
                    try {
                        return reader.docFreq(term) - reader.docFreq(term1);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return 0;
                    }
                }
            });

            for ( ; termEnum.term() != null; termEnum.next()) {
                queue.offer(termEnum.term());
            }
        }

        public boolean next() throws IOException {
            return queue.poll() != null;
        }

        public Term term() {
            return queue.peek();
        }

        public int docFreq() {
            try {
                return reader.docFreq(queue.peek());
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
        }

        public void close() throws IOException {
            Log.debug(SOURCE, "NOT IMPLEMENTED", "close", null);
        }

    }

}
