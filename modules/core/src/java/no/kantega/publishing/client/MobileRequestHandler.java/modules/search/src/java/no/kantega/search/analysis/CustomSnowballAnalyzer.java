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

import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

import java.io.Reader;

/**
 * Date: Mar 17, 2009
 * Time: 6:39:08 AM
 *
 * @author Tarje Killingberg
 */
public class CustomSnowballAnalyzer extends SnowballAnalyzer {

    private static final String SOURCE = CustomSnowballAnalyzer.class.getName();
    private String name;
    private String[] stopwords;


    public CustomSnowballAnalyzer(String name) {
        super(name);
        this.name = name;
    }

    public CustomSnowballAnalyzer(String name, String[] stopwords) {
        super(name, stopwords);
        this.name = name;
        this.stopwords = stopwords;
    }

    public void setStopwords(String[] stopwords) {
        this.stopwords = stopwords;
    }

    @Override
    public TokenStream tokenStream(String s, Reader reader) {
        TokenStream result = new StandardTokenizer(reader);
        result = new StandardFilter(result);
        result = new LowerCaseFilter(result);
        if (stopwords != null) {
            result = new StopFilter(result, stopwords);
        }
        result = new SnowballFilter(result, name);
        return result;
    }

}
