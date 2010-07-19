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

package no.kantega.search.analysis;

import org.apache.lucene.analysis.Analyzer;

import java.util.*;
import java.io.*;

import no.kantega.commons.log.Log;

/**
 *
 */
public class AnalyzerFactory {

    private static final String SOURCE = AnalyzerFactory.class.getName();

    private Analyzer defaultAnalyzer;
    private Map<String, Analyzer> perFieldAnalyzers;
    private StopwordManager stopwordManager = new StopwordManager();


    public Analyzer createInstance() {
        AnalyzerWrapper wrapper = new AnalyzerWrapper(defaultAnalyzer, stopwordManager.getStopwordsAsArray());
        for (String field : perFieldAnalyzers.keySet()) {
            Analyzer analyzer = perFieldAnalyzers.get(field);
            wrapper.addAnalyzer(field, analyzer);
        }
        return wrapper;
    }

    public Map getPerFieldAnalyzers() {
        return perFieldAnalyzers;
    }

    public void setPerFieldAnalyzers(Map<String, Analyzer> perFieldAnalyzers) {
        for (Analyzer a : perFieldAnalyzers.values()) {
            processAnalyzer(a);
        }
        this.perFieldAnalyzers = perFieldAnalyzers;
    }

    public void setDefaultAnalyzer(Analyzer defaultAnalyzer) {
        processAnalyzer(defaultAnalyzer);
        this.defaultAnalyzer = defaultAnalyzer;
    }

    private void processAnalyzer(Analyzer a) {
        if (a instanceof CustomSnowballAnalyzer) {
            ((CustomSnowballAnalyzer)a).setStopwords(stopwordManager.getStopwordsAsArray());
        }
    }

    /**
     * Returnerer alle stoppord i et sortert array.
     *
     * @return alle stoppord.
     */
    public String[] getStopwords() {
        String[] stopWords = stopwordManager.getStopwordsAsArray();
        Arrays.sort(stopWords);
        return stopWords;
    }


    class StopwordManager {

        public static final String STOPWORDS_FILENAME_DEFAULT = "stopwords.default";
        public static final String STOPWORDS_FILENAME_PROJECT = "stopwords.project";
        private Set<String> stopwords = new HashSet<String>();


        public StopwordManager() {
            refreshStopwords();
        }

        public String[] getStopwordsAsArray() {
            return stopwords.toArray(new String[stopwords.size()]);
        }

        public Set<String> getStopwordsAsSet() {
            return new HashSet<String>(stopwords);
        }

        private void refreshStopwords() {
            Set<String> stopwordsSet = new HashSet<String>(readFile(STOPWORDS_FILENAME_DEFAULT));
            stopwordsSet.addAll(readFile(STOPWORDS_FILENAME_PROJECT));
            Log.debug(SOURCE, "Stopwords: " + stopwordsSet, "refreshStopwords", null);
            stopwords = stopwordsSet;
        }

        private Set<String> readFile(String filename) {
            Set<String> stopwordsSet = new HashSet<String>();
            try {
                InputStream is = getClass().getResourceAsStream("/" + filename);
                if (is != null) {
                    stopwordsSet = readStream(is);
                    Log.info(SOURCE, "Read stopwords from file \"" + filename + "\".", "readFile", null);
                } else {
                    Log.info(SOURCE, "InputStream was null for filename: \"" + filename + "\".", "readFile", null);
                }
            } catch (IOException e) {
                Log.error(SOURCE, e, "refreshStopwords", null);
            }
            return stopwordsSet;
        }

        private Set<String> readStream(InputStream stream) throws IOException {
            Set<String> stopwordsSet = new HashSet<String>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "iso-8859-1"));
            String line = reader.readLine();
            for (; line != null; line = reader.readLine()) {
                if (!line.startsWith("#") && !line.trim().equals("")) {
                    stopwordsSet.add(line.trim());
                }
            }
            return stopwordsSet;
        }

    }

}
