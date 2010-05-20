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
package no.kantega.search.query;

import no.kantega.search.AbstractSearchTestCase;
import no.kantega.search.criteria.Criterion;
import no.kantega.search.criteria.TextCriterion;
import no.kantega.search.index.Fields;
import no.kantega.search.index.IndexWriterManager;
import no.kantega.search.result.DocumentHit;
import no.kantega.search.result.SearchResult;

import java.io.IOException;
import java.text.ParseException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.search.Sort;

/**
 *
 * @author Tarje Killingberg
 */
public class SortTest extends AbstractSearchTestCase {


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        addSortableTestDocs(getIndexManager().getIndexWriterManager());
    }

    public void testStringSortAscending() throws IOException, ParseException {
        // Create criteria
        Criterion criterion = new TextCriterion(Fields.CONTENT, "stringsortable", getIndexManager().getAnalyzerFactory().createInstance());

        // Create search query
        SearchQueryDefaultImpl searchQuery = new SearchQueryDefaultImpl();
        searchQuery.addCriterion(criterion);
        searchQuery.setSort(new Sort(Fields.TITLE_UNANALYZED));

        // Search
        SearchResult result = getSearcher().search(searchQuery);

        // Verify result
        List<DocumentHit> documentHits = result.getDocumentHits();
        assertEquals(3, documentHits.size());
        List<String> titleList = toStringList(documentHits, Fields.TITLE_UNANALYZED);
        assertSorted(titleList, false);
    }

    public void testStringSortDescending() throws IOException, ParseException {
        // Create criteria
        Criterion criterion = new TextCriterion(Fields.CONTENT, "stringsortable", getIndexManager().getAnalyzerFactory().createInstance());

        // Create search query
        SearchQueryDefaultImpl searchQuery = new SearchQueryDefaultImpl();
        searchQuery.addCriterion(criterion);
        searchQuery.setSort(new Sort(Fields.TITLE_UNANALYZED, true));

        // Search
        SearchResult result = getSearcher().search(searchQuery);

        // Verify result
        List<DocumentHit> documentHits = result.getDocumentHits();
        assertEquals(3, documentHits.size());
        List<String> titleList = toStringList(documentHits, Fields.TITLE_UNANALYZED);
        assertSorted(titleList, true);
    }

    public void testDateSortAscending() throws IOException, ParseException {
        // Create criteria
        Criterion criterion = new TextCriterion(Fields.CONTENT, "datesortable", getIndexManager().getAnalyzerFactory().createInstance());

        // Create search query
        SearchQueryDefaultImpl searchQuery = new SearchQueryDefaultImpl();
        searchQuery.addCriterion(criterion);
        searchQuery.setSort(new Sort(Fields.LAST_MODIFIED));

        // Search
        SearchResult result = getSearcher().search(searchQuery);

        // Verify result
        List<DocumentHit> documentHits = result.getDocumentHits();
        assertEquals(3, documentHits.size());
        List<String> lastModifiedList = toStringList(documentHits, Fields.LAST_MODIFIED);
        assertSorted(lastModifiedList, false);
    }

    public void testDateSortDescending() throws IOException, ParseException {
        // Create criteria
        Criterion criterion = new TextCriterion(Fields.CONTENT, "datesortable", getIndexManager().getAnalyzerFactory().createInstance());

        // Create search query
        SearchQueryDefaultImpl searchQuery = new SearchQueryDefaultImpl();
        searchQuery.addCriterion(criterion);
        searchQuery.setSort(new Sort(Fields.LAST_MODIFIED, true));

        // Search
        SearchResult result = getSearcher().search(searchQuery);

        // Verify result
        List<DocumentHit> documentHits = result.getDocumentHits();
        assertEquals(3, documentHits.size());
        List<String> lastModifiedList = toStringList(documentHits, Fields.LAST_MODIFIED);
        assertSorted(lastModifiedList, true);
    }

    private List<String> toStringList(List<DocumentHit> documentHits, String field) {
        List<String> retVal = new ArrayList<String>();
        for (DocumentHit hit : documentHits) {
            retVal.add(hit.getDocument().get(field));
        }
        return retVal;
    }

    private static void assertSorted(List<String> list, boolean reverse) {
        if (reverse) {
            Collections.reverse(list);
        }
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i-1).compareTo(list.get(i)) > 0) {
                fail("\"" + list.get(i-1) + "\" is not less than \"" + list.get(i) + "\".");
            }
        }
    }

    private void addSortableTestDocs(IndexWriterManager manager) throws IOException, ParseException {
        IndexWriter writer = manager.getIndexWriter("aksess", true);
        // add string sortable test docs
        {
            Document doc = new Document();
            doc.add(new Field(Fields.TITLE_UNANALYZED, "Begins with B does this title", Field.Store.YES, Field.Index.NOT_ANALYZED));
            doc.add(new Field(Fields.CONTENT, "stringsortable", Field.Store.NO, Field.Index.ANALYZED));
            writer.addDocument(doc);
        }
        {
            Document doc = new Document();
            doc.add(new Field(Fields.TITLE_UNANALYZED, "A title that begins with A", Field.Store.YES, Field.Index.NOT_ANALYZED));
            doc.add(new Field(Fields.CONTENT, "stringsortable", Field.Store.NO, Field.Index.ANALYZED));
            writer.addDocument(doc);
        }
        {
            Document doc = new Document();
            doc.add(new Field(Fields.TITLE_UNANALYZED, "Creative title that begins with C", Field.Store.YES, Field.Index.NOT_ANALYZED));
            doc.add(new Field(Fields.CONTENT, "stringsortable", Field.Store.NO, Field.Index.ANALYZED));
            writer.addDocument(doc);
        }

        // add date sortable test docs
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        {
            Document doc = new Document();
            doc.add(new Field(Fields.CONTENT, "This datesortable document is in the middle", Field.Store.NO, Field.Index.ANALYZED));
            doc.add(new Field(Fields.LAST_MODIFIED, DateTools.dateToString(df.parse("2008-05-30"), DateTools.Resolution.MINUTE), Field.Store.YES, Field.Index.NOT_ANALYZED));
            writer.addDocument(doc);
        }
        {
            Document doc = new Document();
            doc.add(new Field(Fields.CONTENT, "This datesortable document was last modified before", Field.Store.NO, Field.Index.ANALYZED));
            doc.add(new Field(Fields.LAST_MODIFIED, DateTools.dateToString(df.parse("2007-02-01"), DateTools.Resolution.MINUTE), Field.Store.YES, Field.Index.NOT_ANALYZED));
            writer.addDocument(doc);
        }
        {
            Document doc = new Document();
            doc.add(new Field(Fields.CONTENT, "This datesortable document was last modified later", Field.Store.NO, Field.Index.ANALYZED));
            doc.add(new Field(Fields.LAST_MODIFIED, DateTools.dateToString(df.parse("2008-05-31"), DateTools.Resolution.MINUTE), Field.Store.YES, Field.Index.NOT_ANALYZED));
            writer.addDocument(doc);
        }
        manager.ensureClosed("aksess");
    }

}
