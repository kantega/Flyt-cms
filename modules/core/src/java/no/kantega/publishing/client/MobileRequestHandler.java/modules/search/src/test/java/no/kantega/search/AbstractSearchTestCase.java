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

package no.kantega.search;

import junit.framework.TestCase;
import no.kantega.search.analysis.AnalyzerFactory;
import no.kantega.search.core.Searcher;
import no.kantega.search.core.SearcherImpl;
import no.kantega.search.index.*;
import no.kantega.search.index.config.LuceneConfiguration;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.search.spell.LuceneDictionary;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Date: Jan 16, 2009
 * Time: 7:12:39 AM
 *
 * @author Tarje Killingberg
 */
public abstract class AbstractSearchTestCase extends TestCase {

    private static final String SOURCE = AbstractSearchTestCase.class.getName();
    protected static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private IndexManager indexManager;
    private Searcher searcher;
    private String indexPath = AbstractSearchTestCase.class.getResource("index").getFile();


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        LogConfigurator.configure();

        DirectoryManager directoryManager = new RAMDirectoryManager();

        IndexManagerImpl indexManager = new IndexManagerImpl();

        AnalyzerFactory analyzerFactory = new AnalyzerFactory();
        analyzerFactory.setDefaultAnalyzer(new StandardAnalyzer());
        Map<String, Analyzer> perFieldAnalyzers = new HashMap<String, Analyzer>();
        perFieldAnalyzers.put("Content", new SnowballAnalyzer("Norwegian"));
        analyzerFactory.setPerFieldAnalyzers(perFieldAnalyzers);
        indexManager.setAnalyzerFactory(analyzerFactory);

//        indexManager.setDocumentProviderSelector();

        IndexJobManager indexJobManager = new IndexJobManager();
        indexJobManager.init();
        indexManager.setIndexJobManager(indexJobManager);


        IndexReaderManager indexReaderManager = new IndexReaderManager();
        indexReaderManager.setDirectoryManager(directoryManager);
        indexManager.setIndexReaderManager(indexReaderManager);

        IndexSearcherManager indexSearcherManager = new IndexSearcherManager();
        indexSearcherManager.setDirectoryManager(directoryManager);
        indexSearcherManager.setSearcherTimeout(-1);
        indexManager.setIndexSearcherManager(indexSearcherManager);

        IndexWriterManager indexWriterManager = new IndexWriterManager();
        indexWriterManager.setDirectoryManager(directoryManager);
        indexManager.setIndexWriterManager(indexWriterManager);
        this.setIndexManager(indexManager);

        SearcherImpl searcher = new SearcherImpl();
        searcher.setIndexManager(indexManager);
        this.setSearcher(searcher);

        addDocuments(indexWriterManager, analyzerFactory.createInstance());

        SpellChecker spellChecker = new SpellChecker(directoryManager.getDirectory("spelling"));

        spellChecker.indexDictionary(new LuceneDictionary(indexReaderManager.getReader("aksess"), Fields.CONTENT_UNSTEMMED));
    }

    private void addDocuments(IndexWriterManager indexWriterManager, Analyzer instance) {
        try {
            final IndexWriter writer = indexWriterManager.getIndexWriter("aksess", true);

            try {
                addDocument(writer, Fields.CONTENT, "Dette er en speilvegg", Field.Store.NO, Field.Index.ANALYZED);
                addDocument(writer, Fields.CONTENT, "Dette er også en speilvegg", Field.Store.NO, Field.Index.ANALYZED);

                // SuggestionTest
                addDocument(writer, Fields.CONTENT_UNSTEMMED, "trondheimsfjorden", Field.Store.NO, Field.Index.ANALYZED);
                addDocument(writer, Fields.CONTENT_UNSTEMMED, "trondheimsfjapp", Field.Store.NO, Field.Index.ANALYZED);
                addDocument(writer, Fields.CONTENT_UNSTEMMED, "trondheimsfart", Field.Store.NO, Field.Index.ANALYZED);
                addDocument(writer, Fields.CONTENT_UNSTEMMED, "trondheimsfiske", Field.Store.NO, Field.Index.ANALYZED);
                addDocument(writer, Fields.CONTENT_UNSTEMMED, "trondheimsfakta", Field.Store.NO, Field.Index.ANALYZED);
                addDocument(writer, Fields.CONTENT_UNSTEMMED, "trondheim kommune", Field.Store.NO, Field.Index.ANALYZED);
                addDocument(writer, Fields.CONTENT_UNSTEMMED, "trondheim kommunal", Field.Store.NO, Field.Index.ANALYZED);
                addDocument(writer, Fields.CONTENT_UNSTEMMED, "trondheim kommunehus", Field.Store.NO, Field.Index.ANALYZED);
                addDocument(writer, Fields.CONTENT_UNSTEMMED, "trondheim kommunesjef", Field.Store.NO, Field.Index.ANALYZED);
                addDocument(writer, Fields.CONTENT_UNSTEMMED, "trondheim kommuneadministrasjon", Field.Store.NO, Field.Index.ANALYZED);
                addDocument(writer, Fields.CONTENT_UNSTEMMED, "trondhjem kommuneadministrasjon", Field.Store.NO, Field.Index.ANALYZED);
                addDocument(writer, Fields.CONTENT_UNSTEMMED, "trondheim trondhjem trondhjem trondheim trondheim", Field.Store.NO, Field.Index.ANALYZED);


                addLastModifiedWithWord(writer);
                addLastModified(writer);
                addLastModifiedBounds(writer);

                // HitCountQueryTest
                addHitCountQueryTestDocs(writer);


            } catch (ParseException e) {
                throw new RuntimeException(e);
            } finally {
                if(writer != null) {
                    indexWriterManager.ensureClosed("aksess");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addHitCountQueryTestDocs(IndexWriter writer) throws IOException, ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        {
            Document doc = new Document();
            doc.add(new Field(Fields.CONTENT, "This trondheim document has contenttemplateid 1", Field.Store.NO, Field.Index.ANALYZED));
            doc.add(new Field(Fields.CONTENT_TEMPLATE_ID, Integer.toString(1), Field.Store.NO, Field.Index.NOT_ANALYZED));
            doc.add(new Field(Fields.LAST_MODIFIED, DateTools.dateToString(df.parse("2001-01-30"), DateTools.Resolution.MINUTE), Field.Store.YES, Field.Index.NOT_ANALYZED));
            writer.addDocument(doc);
        }
        {

            Document doc = new Document();
            doc.add(new Field(Fields.CONTENT, "This trondheim document has contenttemplateid 5", Field.Store.NO, Field.Index.ANALYZED));
            doc.add(new Field(Fields.CONTENT_TEMPLATE_ID, Integer.toString(5), Field.Store.NO, Field.Index.NOT_ANALYZED));
            doc.add(new Field(Fields.LAST_MODIFIED, DateTools.dateToString(df.parse("2001-02-30"), DateTools.Resolution.MINUTE), Field.Store.YES, Field.Index.NOT_ANALYZED));
            writer.addDocument(doc);
        }
        {
            Document doc = new Document();
            doc.add(new Field(Fields.CONTENT, "This trondheim document has contenttemplateid 10", Field.Store.NO, Field.Index.ANALYZED));
            doc.add(new Field(Fields.CONTENT_TEMPLATE_ID, Integer.toString(10), Field.Store.NO, Field.Index.NOT_ANALYZED));
            doc.add(new Field(Fields.LAST_MODIFIED, DateTools.dateToString(df.parse("2001-03-30"), DateTools.Resolution.MINUTE), Field.Store.YES, Field.Index.NOT_ANALYZED));
            writer.addDocument(doc);
        }
        {
            Document doc = new Document();
            doc.add(new Field(Fields.CONTENT, "This trondheim document has contenttemplateid 30", Field.Store.NO, Field.Index.ANALYZED));
            doc.add(new Field(Fields.CONTENT_TEMPLATE_ID, Integer.toString(30), Field.Store.NO, Field.Index.NOT_ANALYZED));
            doc.add(new Field(Fields.LAST_MODIFIED, DateTools.dateToString(df.parse("2001-04-30"), DateTools.Resolution.MINUTE), Field.Store.YES, Field.Index.NOT_ANALYZED));
            writer.addDocument(doc);
        }


        {
            Document doc = new Document();
            doc.add(new Field(Fields.CONTENT, "This nidelven document has contenttemplateid 1", Field.Store.NO, Field.Index.ANALYZED));
            doc.add(new Field(Fields.CONTENT_TEMPLATE_ID, Integer.toString(1), Field.Store.NO, Field.Index.NOT_ANALYZED));
            doc.add(new Field(Fields.DOCTYPE, "Content", Field.Store.YES, Field.Index.NOT_ANALYZED));
            writer.addDocument(doc);
        }
        {

            Document doc = new Document();
            doc.add(new Field(Fields.CONTENT, "This nidelven document has contenttemplateid 5", Field.Store.NO, Field.Index.ANALYZED));
            doc.add(new Field(Fields.CONTENT_TEMPLATE_ID, Integer.toString(5), Field.Store.NO, Field.Index.NOT_ANALYZED));
            doc.add(new Field(Fields.DOCTYPE, "Content", Field.Store.YES, Field.Index.NOT_ANALYZED));
            writer.addDocument(doc);
        }
        {
            Document doc = new Document();
            doc.add(new Field(Fields.CONTENT, "This nidelven document has contenttemplateid 10", Field.Store.NO, Field.Index.ANALYZED));
            doc.add(new Field(Fields.CONTENT_TEMPLATE_ID, Integer.toString(10), Field.Store.NO, Field.Index.NOT_ANALYZED));
            doc.add(new Field(Fields.DOCTYPE, "Content", Field.Store.YES, Field.Index.NOT_ANALYZED));
            writer.addDocument(doc);
        }
        {
            Document doc = new Document();
            doc.add(new Field(Fields.CONTENT, "This nidelven document has contenttemplateid 30", Field.Store.NO, Field.Index.ANALYZED));
            doc.add(new Field(Fields.CONTENT_TEMPLATE_ID, Integer.toString(30), Field.Store.NO, Field.Index.NOT_ANALYZED));
            doc.add(new Field(Fields.DOCTYPE, "Content", Field.Store.YES, Field.Index.NOT_ANALYZED));
            writer.addDocument(doc);
        }

    }

    private void addLastModifiedBounds(IndexWriter writer) throws IOException, ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        {
            Document doc = new Document();
            doc.add(new Field(Fields.CONTENT, "Contains spasertrur", Field.Store.NO, Field.Index.ANALYZED));
            doc.add(new Field(Fields.LAST_MODIFIED, DateTools.dateToString(df.parse("2008-05-30"), DateTools.Resolution.MINUTE), Field.Store.YES, Field.Index.NOT_ANALYZED));
            writer.addDocument(doc);
        }
        {
            Document doc = new Document();
            doc.add(new Field(Fields.CONTENT, "Contains spasertrur", Field.Store.NO, Field.Index.ANALYZED));
            doc.add(new Field(Fields.LAST_MODIFIED, DateTools.dateToString(df.parse("2008-05-06"), DateTools.Resolution.MINUTE), Field.Store.YES, Field.Index.NOT_ANALYZED));
            writer.addDocument(doc);
        }
    }

    private void addLastModifiedWithWord(IndexWriter writer) throws ParseException, IOException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Document doc = new Document();
        doc.add(new Field(Fields.CONTENT, "Contains spasertrur", Field.Store.NO, Field.Index.ANALYZED));
        doc.add(new Field(Fields.LAST_MODIFIED, DateTools.dateToString(df.parse("2005-09-10"), DateTools.Resolution.MINUTE), Field.Store.YES, Field.Index.NOT_ANALYZED));
        writer.addDocument(doc);
    }

    private void addLastModified(IndexWriter writer) throws ParseException, IOException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Document doc = new Document();
        doc.add(new Field(Fields.CONTENT, "Contains text", Field.Store.NO, Field.Index.ANALYZED));
        doc.add(new Field(Fields.LAST_MODIFIED, DateTools.dateToString(df.parse("2008-04-11"), DateTools.Resolution.MINUTE), Field.Store.YES, Field.Index.NOT_ANALYZED));
        writer.addDocument(doc);
    }

    private void addDocument(IndexWriter writer, String field, String value, Field.Store store, Field.Index index) throws IOException {
        Document doc = new Document();
        doc.add(new Field(field, value, store, index));
        writer.addDocument(doc);
    }

    protected int getIndexSize() throws IOException {
        return indexManager.getIndexReaderManager().getReader("aksess").numDocs();
    }

    protected IndexManager getIndexManager() {
        return indexManager;
    }

    private void setIndexManager(IndexManager indexManager) {
        this.indexManager = indexManager;
    }

    protected Searcher getSearcher() {
        return searcher;
    }

    private void setSearcher(Searcher searcher) {
        this.searcher = searcher;
    }

}
