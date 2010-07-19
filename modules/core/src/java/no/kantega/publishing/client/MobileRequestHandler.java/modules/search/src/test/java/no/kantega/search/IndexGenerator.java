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

import no.kantega.search.index.Fields;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Date: Dec 5, 2008
 * Time: 2:11:40 PM
 *
 * @author Tarje Killingberg
 * @deprecated
 */
public class IndexGenerator {

    private static final String SOURCE = IndexGenerator.class.getName();
    public static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");;

    public static void generateIndex(String indexPath) throws IOException, ParseException {
        System.out.println("Generating temporary index at path: " + indexPath);
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriter indexWriter = new IndexWriter(indexPath, analyzer, true, new IndexWriter.MaxFieldLength(100));

        List<DocumentWrapper> docWrappers = new IndexGenerator().generateDocWrappers();
        for (DocumentWrapper docWrapper : docWrappers) {
            indexWriter.addDocument(docWrapper.getDocument());
        }
        indexWriter.commit();
        indexWriter.close();
    }

    private List<DocumentWrapper> generateDocWrappers() throws ParseException {
        List<DocumentWrapper> documents = new ArrayList<DocumentWrapper>();

        DocumentWrapper docWrapper = new DocumentWrapper("Content", "100", "Lorem Ipsum", "1",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer lorem turpis, bibendum id, ultrices at, posuere sed, erat. Sed vestibulum placerat felis. Nam porta consectetur tellus. Nunc ac odio vel orci porttitor pretium. Donec hendrerit erat non mi. Praesent metus. Suspendisse potenti. Donec et lacus. Nulla a mauris. Etiam condimentum dignissim tellus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus dui erat, consectetur eu, feugiat et, egestas in, arcu. Mauris et neque quis risus adipiscing sagittis. Etiam interdum orci eget libero euismod viverra. Mauris eget massa a turpis sodales suscipit. Nunc neque nulla, commodo a, mollis dapibus, pellentesque rhoncus, enim. Proin sit amet dolor in nisl aliquam malesuada. Maecenas id libero. Suspendisse euismod nisi sed velit.",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer lorem turpis, bibendum id, ultrices at, posuere sed, erat. Sed vestibulum placerat felis. Nam porta consectetur tellus. Nunc ac odio vel orci porttitor pretium. Donec hendrerit erat non mi. Praesent metus. Suspendisse potenti. Donec et lacus. Nulla a mauris. Etiam condimentum dignissim tellus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus dui erat, consectetur eu, feugiat et, egestas in, arcu. Mauris et neque quis risus adipiscing sagittis. Etiam interdum orci eget libero euismod viverra. Mauris eget massa a turpis sodales suscipit. Nunc neque nulla, commodo a, mollis dapibus, pellentesque rhoncus, enim. Proin sit amet dolor in nisl aliquam malesuada. Maecenas id libero. Suspendisse euismod nisi sed velit.",
                "Lorem ipsum consectetur posuere", DateTools.dateToString(dateFormat.parse("2008-05-24"), DateTools.Resolution.MINUTE), "0", "1 2 1 7",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer lorem turpis, bibendum id, ultrices at, posuere sed, erat.",
                "Lorem ipsum", "1", "10", "30", "10");
        documents.add(docWrapper);
        docWrapper = new DocumentWrapper("Content", "101", "Lorem Ipsum", "1",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer lorem turpis, bibendum id, ultrices at, posuere sed, erat. Sed vestibulum placerat felis. Nam porta consectetur tellus. Nunc ac odio vl ori pttitor pretium. Donec hendrerit erat non mi. Praesent metus. Suspendisse potenti. Donec et lacus. Nulla a mauris. Etiam condimentum dignissim tellus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus dui erat, consectetur eu, feugiat et, egestas in, arcu. Mauris et neque quis risus adipiscing sagittis. Etiam interdum orci eget libero euismod viverra. Mauris eget massa a turpis sodales suscipit. Nunc neque nulla, commodo a, mollis dapibus, pellentesque rhoncus, enim. Proin sit amet dolor in nisl aliquam malesuada. Maecenas id libero. Suspendisse euismod nisi sed velit.",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer lorem turpis, bibendum id, ultrices at, posuere sed, erat. Sed vestibulum placerat felis. Nam porta consectetur tellus. Nunc ac odio vl ori pttitor pretium. Donec hendrerit erat non mi. Praesent metus. Suspendisse potenti. Donec et lacus. Nulla a mauris. Etiam condimentum dignissim tellus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus dui erat, consectetur eu, feugiat et, egestas in, arcu. Mauris et neque quis risus adipiscing sagittis. Etiam interdum orci eget libero euismod viverra. Mauris eget massa a turpis sodales suscipit. Nunc neque nulla, commodo a, mollis dapibus, pellentesque rhoncus, enim. Proin sit amet dolor in nisl aliquam malesuada. Maecenas id libero. Suspendisse euismod nisi sed velit.",
                "Lorem ipsum consectetur posuere", DateTools.dateToString(dateFormat.parse("2006-11-03"), DateTools.Resolution.MINUTE), "0", "1 2 1 7",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer lorem turpis, bibendum id, ultrices at, posuere sed, erat.",
                "Lorem ipsum", "100", "10", "30", "10");
        documents.add(docWrapper);
        docWrapper = new DocumentWrapper("Content", "102", "Lorem Ipsum", "1",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer lorem turpis, bibendum id, ultrices at, posuere sed, erat. Sed vestibulum placerat felis. Nam porta consectetur tellus. Nunc ac odio vel oci porttitor pretium. Donec hendrerit erat non mi. Praesent metus. Suspendisse potenti. Donec et lacus. Nulla a mauris. Etiam condimentum dignissim tellus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus dui erat, consectetur eu, feugiat et, egestas in, arcu. Mauris et neque quis risus adipiscing sagittis. Etiam interdum orci eget libero euismod viverra. Mauris eget massa a turpis sodales suscipit. Nunc neque nulla, commodo a, mollis dapibus, pellentesque rhoncus, enim. Proin sit amet dolor in nisl aliquam malesuada. Maecenas id libero. Suspendisse euismod nisi sed velit.",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer lorem turpis, bibendum id, ultrices at, posuere sed, erat. Sed vestibulum placerat felis. Nam porta consectetur tellus. Nunc ac odio vel oci porttitor pretium. Donec hendrerit erat non mi. Praesent metus. Suspendisse potenti. Donec et lacus. Nulla a mauris. Etiam condimentum dignissim tellus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus dui erat, consectetur eu, feugiat et, egestas in, arcu. Mauris et neque quis risus adipiscing sagittis. Etiam interdum orci eget libero euismod viverra. Mauris eget massa a turpis sodales suscipit. Nunc neque nulla, commodo a, mollis dapibus, pellentesque rhoncus, enim. Proin sit amet dolor in nisl aliquam malesuada. Maecenas id libero. Suspendisse euismod nisi sed velit.",
                "Lorem ipsum consectetur posuere", DateTools.dateToString(dateFormat.parse("2008-02-12"), DateTools.Resolution.MINUTE), "0", "1 2 1 7",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer lorem turpis, bibendum id, ultrices at, posuere sed, erat.",
                "Lorem ipsum", "100 101", "10", "30", "10");
        documents.add(docWrapper);
        docWrapper = new DocumentWrapper("Content", "103", "Lorem Ipsum", "1",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer lorem turpis, bibendum id, ultrices at, posuere sed, erat. Sed vestibulum placerat felis. Nam porta consectetur tellus. Nunc ac odio vel or porttitor pretium. Donec hendrerit erat non mi. Praesent metus. Suspendisse potenti. Donec et lacus. Nulla a mauris. Etiam condimentum dignissim tellus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus dui erat, consectetur eu, feugiat et, egestas in, arcu. Mauris et neque quis risus adipiscing sagittis. Etiam interdum orci eget libero euismod viverra. Mauris eget massa a turpis sodales suscipit. Nunc neque nulla, commodo a, mollis dapibus, pellentesque rhoncus, enim. Proin sit amet dolor in nisl aliquam malesuada. Maecenas id libero. Suspendisse euismod nisi sed velit.",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer lorem turpis, bibendum id, ultrices at, posuere sed, erat. Sed vestibulum placerat felis. Nam porta consectetur tellus. Nunc ac odio vel or porttitor pretium. Donec hendrerit erat non mi. Praesent metus. Suspendisse potenti. Donec et lacus. Nulla a mauris. Etiam condimentum dignissim tellus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus dui erat, consectetur eu, feugiat et, egestas in, arcu. Mauris et neque quis risus adipiscing sagittis. Etiam interdum orci eget libero euismod viverra. Mauris eget massa a turpis sodales suscipit. Nunc neque nulla, commodo a, mollis dapibus, pellentesque rhoncus, enim. Proin sit amet dolor in nisl aliquam malesuada. Maecenas id libero. Suspendisse euismod nisi sed velit.",
                "Lorem ipsum consectetur posuere", DateTools.dateToString(dateFormat.parse("2008-07-16"), DateTools.Resolution.MINUTE), "0", "1 2 1 7",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer lorem turpis, bibendum id, ultrices at, posuere sed, erat.",
                "Lorem ipsum", "100", "10", "30", "10");
        documents.add(docWrapper);
        docWrapper = new DocumentWrapper("Content", "104", "Lorem Ipsum", "1",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer lorem turpis, bibendum id, ultrices at, posuere sed, erat. Sed vestibulum placerat felis. Nam porta consectetur tellus. Nunc ac odio v orci porttitor pretium. Donec hendrerit erat non mi. Praesent metus. Suspendisse potenti. Donec et lacus. Nulla a mauris. Etiam condimentum dignissim tellus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus dui erat, consectetur eu, feugiat et, egestas in, arcu. Mauris et neque quis risus adipiscing sagittis. Etiam interdum orci eget libero euismod viverra. Mauris eget massa a turpis sodales suscipit. Nunc neque nulla, commodo a, mollis dapibus, pellentesque rhoncus, enim. Proin sit amet dolor in nisl aliquam malesuada. Maecenas id libero. Suspendisse euismod nisi sed velit.",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer lorem turpis, bibendum id, ultrices at, posuere sed, erat. Sed vestibulum placerat felis. Nam porta consectetur tellus. Nunc ac odio v orci porttitor pretium. Donec hendrerit erat non mi. Praesent metus. Suspendisse potenti. Donec et lacus. Nulla a mauris. Etiam condimentum dignissim tellus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus dui erat, consectetur eu, feugiat et, egestas in, arcu. Mauris et neque quis risus adipiscing sagittis. Etiam interdum orci eget libero euismod viverra. Mauris eget massa a turpis sodales suscipit. Nunc neque nulla, commodo a, mollis dapibus, pellentesque rhoncus, enim. Proin sit amet dolor in nisl aliquam malesuada. Maecenas id libero. Suspendisse euismod nisi sed velit.",
                "Lorem ipsum consectetur posuere", DateTools.dateToString(dateFormat.parse("2008-01-12"), DateTools.Resolution.MINUTE), "0", "1 2 1 7",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer lorem turpis, bibendum id, ultrices at, posuere sed, erat.",
                "Lorem ipsum", "100 103", "10", "30", "10");
        documents.add(docWrapper);
        docWrapper = new DocumentWrapper("Content", "105", "Lorem Ipsum", "1",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer lorem turpis, bibendum id, ultrices at, posuere sed, erat. Sed vestibulum placerat felis. Nam porta consectetur tellus. Nunc ac odio vel orci poitor pretium. Donec hendrerit erat non mi. Praesent metus. Suspendisse potenti. Donec et lacus. Nulla a mauris. Etiam condimentum dignissim tellus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus dui erat, consectetur eu, feugiat et, egestas in, arcu. Mauris et neque quis risus adipiscing sagittis. Etiam interdum orci eget libero euismod viverra. Mauris eget massa a turpis sodales suscipit. Nunc neque nulla, commodo a, mollis dapibus, pellentesque rhoncus, enim. Proin sit amet dolor in nisl aliquam malesuada. Maecenas id libero. Suspendisse euismod nisi sed velit.",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer lorem turpis, bibendum id, ultrices at, posuere sed, erat. Sed vestibulum placerat felis. Nam porta consectetur tellus. Nunc ac odio vel orci poitor pretium. Donec hendrerit erat non mi. Praesent metus. Suspendisse potenti. Donec et lacus. Nulla a mauris. Etiam condimentum dignissim tellus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus dui erat, consectetur eu, feugiat et, egestas in, arcu. Mauris et neque quis risus adipiscing sagittis. Etiam interdum orci eget libero euismod viverra. Mauris eget massa a turpis sodales suscipit. Nunc neque nulla, commodo a, mollis dapibus, pellentesque rhoncus, enim. Proin sit amet dolor in nisl aliquam malesuada. Maecenas id libero. Suspendisse euismod nisi sed velit.",
                "Lorem ipsum consectetur posuere", DateTools.dateToString(dateFormat.parse("2008-02-25"), DateTools.Resolution.MINUTE), "0", "1 2 1 7",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer lorem turpis, bibendum id, ultrices at, posuere sed, erat.",
                "Lorem ipsum", "100 103 104", "10", "30", "10");
        documents.add(docWrapper);
        docWrapper = new DocumentWrapper("Content", "106", "Lorem Ipsum", "1",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer lorem turpis, bibendum id, ultrices at, posuere sed, erat. Sed vestibulum placerat felis. Nam porta consectetur tellus. Nunc ac odio vel or potitor pretium. Donec hendrerit erat non mi. Praesent metus. Suspendisse potenti. Donec et lacus. Nulla a mauris. Etiam condimentum dignissim tellus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus dui erat, consectetur eu, feugiat et, egestas in, arcu. Mauris et neque quis risus adipiscing sagittis. Etiam interdum orci eget libero euismod viverra. Mauris eget massa a turpis sodales suscipit. Nunc neque nulla, commodo a, mollis dapibus, pellentesque rhoncus, enim. Proin sit amet dolor in nisl aliquam malesuada. Maecenas id libero. Suspendisse euismod nisi sed velit.",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer lorem turpis, bibendum id, ultrices at, posuere sed, erat. Sed vestibulum placerat felis. Nam porta consectetur tellus. Nunc ac odio vel or potitor pretium. Donec hendrerit erat non mi. Praesent metus. Suspendisse potenti. Donec et lacus. Nulla a mauris. Etiam condimentum dignissim tellus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus dui erat, consectetur eu, feugiat et, egestas in, arcu. Mauris et neque quis risus adipiscing sagittis. Etiam interdum orci eget libero euismod viverra. Mauris eget massa a turpis sodales suscipit. Nunc neque nulla, commodo a, mollis dapibus, pellentesque rhoncus, enim. Proin sit amet dolor in nisl aliquam malesuada. Maecenas id libero. Suspendisse euismod nisi sed velit.",
                "Lorem ipsum consectetur posuere", DateTools.dateToString(dateFormat.parse("2008-08-01"), DateTools.Resolution.MINUTE), "0", "1 2 1 7",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer lorem turpis, bibendum id, ultrices at, posuere sed, erat.",
                "Lorem ipsum", "100 101 102", "10", "30", "10");
        documents.add(docWrapper);
        docWrapper = new DocumentWrapper("Content", "107", "Lorem Ipsum", "1",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer lorem turpis, bibendum id, ultrices at, posuere sed, erat. Sed vestibulum placerat felis. Nam porta consectetur tellus. Nunc ac odio vel orci por pretium. Donec hendrerit erat non mi. Praesent metus. Suspendisse potenti. Donec et lacus. Nulla a mauris. Etiam condimentum dignissim tellus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus dui erat, consectetur eu, feugiat et, egestas in, arcu. Mauris et neque quis risus adipiscing sagittis. Etiam interdum orci eget libero euismod viverra. Mauris eget massa a turpis sodales suscipit. Nunc neque nulla, commodo a, mollis dapibus, pellentesque rhoncus, enim. Proin sit amet dolor in nisl aliquam malesuada. Maecenas id libero. Suspendisse euismod nisi sed velit.",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer lorem turpis, bibendum id, ultrices at, posuere sed, erat. Sed vestibulum placerat felis. Nam porta consectetur tellus. Nunc ac odio vel orci por pretium. Donec hendrerit erat non mi. Praesent metus. Suspendisse potenti. Donec et lacus. Nulla a mauris. Etiam condimentum dignissim tellus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus dui erat, consectetur eu, feugiat et, egestas in, arcu. Mauris et neque quis risus adipiscing sagittis. Etiam interdum orci eget libero euismod viverra. Mauris eget massa a turpis sodales suscipit. Nunc neque nulla, commodo a, mollis dapibus, pellentesque rhoncus, enim. Proin sit amet dolor in nisl aliquam malesuada. Maecenas id libero. Suspendisse euismod nisi sed velit.",
                "Lorem ipsum consectetur posuere", DateTools.dateToString(dateFormat.parse("2007-11-08"), DateTools.Resolution.MINUTE), "0", "1 2 1 7",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer lorem turpis, bibendum id, ultrices at, posuere sed, erat.",
                "Lorem ipsum", "100 101 102 106", "10", "30", "10");
        documents.add(docWrapper);
        docWrapper = new DocumentWrapper("Content", "108", "Lorem Ipsum", "1",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer lorem turpis, bibendum id, ultrices at, posuere sed, erat. Sed vestibulum placerat felis. Nam porta consectetur tellus. Nunc ac odio vl oi portttor pretium. Donec hendrerit erat non mi. Praesent metus. Suspendisse potenti. Donec et lacus. Nulla a mauris. Etiam condimentum dignissim tellus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus dui erat, consectetur eu, feugiat et, egestas in, arcu. Mauris et neque quis risus adipiscing sagittis. Etiam interdum orci eget libero euismod viverra. Mauris eget massa a turpis sodales suscipit. Nunc neque nulla, commodo a, mollis dapibus, pellentesque rhoncus, enim. Proin sit amet dolor in nisl aliquam malesuada. Maecenas id libero. Suspendisse euismod nisi sed velit.",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer lorem turpis, bibendum id, ultrices at, posuere sed, erat. Sed vestibulum placerat felis. Nam porta consectetur tellus. Nunc ac odio vl oi portttor pretium. Donec hendrerit erat non mi. Praesent metus. Suspendisse potenti. Donec et lacus. Nulla a mauris. Etiam condimentum dignissim tellus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus dui erat, consectetur eu, feugiat et, egestas in, arcu. Mauris et neque quis risus adipiscing sagittis. Etiam interdum orci eget libero euismod viverra. Mauris eget massa a turpis sodales suscipit. Nunc neque nulla, commodo a, mollis dapibus, pellentesque rhoncus, enim. Proin sit amet dolor in nisl aliquam malesuada. Maecenas id libero. Suspendisse euismod nisi sed velit.",
                "Lorem ipsum consectetur posuere", DateTools.dateToString(dateFormat.parse("2005-03-12"), DateTools.Resolution.MINUTE), "0", "1 2 1 7",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer lorem turpis, bibendum id, ultrices at, posuere sed, erat.",
                "Lorem ipsum", "100", "10", "30", "10");
        documents.add(docWrapper);
        docWrapper = new DocumentWrapper("Content", "109", "Lorem Ipsum", "1",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer lorem turpis, bibendum id, ultrices at, posuere sed, erat. Sed vestibulum placerat felis. Nam porta consectetur tellus. Nunc ac odio vel oi portti pretium. Donec hendrerit erat non mi. Praesent metus. Suspendisse potenti. Donec et lacus. Nulla a mauris. Etiam condimentum dignissim tellus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus dui erat, consectetur eu, feugiat et, egestas in, arcu. Mauris et neque quis risus adipiscing sagittis. Etiam interdum orci eget libero euismod viverra. Mauris eget massa a turpis sodales suscipit. Nunc neque nulla, commodo a, mollis dapibus, pellentesque rhoncus, enim. Proin sit amet dolor in nisl aliquam malesuada. Maecenas id libero. Suspendisse euismod nisi sed velit.",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer lorem turpis, bibendum id, ultrices at, posuere sed, erat. Sed vestibulum placerat felis. Nam porta consectetur tellus. Nunc ac odio vel oi portti pretium. Donec hendrerit erat non mi. Praesent metus. Suspendisse potenti. Donec et lacus. Nulla a mauris. Etiam condimentum dignissim tellus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus dui erat, consectetur eu, feugiat et, egestas in, arcu. Mauris et neque quis risus adipiscing sagittis. Etiam interdum orci eget libero euismod viverra. Mauris eget massa a turpis sodales suscipit. Nunc neque nulla, commodo a, mollis dapibus, pellentesque rhoncus, enim. Proin sit amet dolor in nisl aliquam malesuada. Maecenas id libero. Suspendisse euismod nisi sed velit.",
                "Lorem ipsum consectetur posuere", DateTools.dateToString(dateFormat.parse("2007-05-10"), DateTools.Resolution.MINUTE), "0", "1 2 1 7",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer lorem turpis, bibendum id, ultrices at, posuere sed, erat.",
                "Lorem ipsum", "100", "10", "30", "10");
        documents.add(docWrapper);

        return documents;
    }

    class DocumentWrapper {

        private Document doc;


        public DocumentWrapper(String docType, String contentId, String title, String siteId, String content, String contentUnstemmed, String keywords, String lastModified, String language, String category, String summary, String tmTopics, String contentParents, String contentTemplateId, String contentStatus, String contentVisibilityStatus) {
            doc = new Document();
            doc.add(new Field(Fields.DOCTYPE, docType, Field.Store.YES, Field.Index.UN_TOKENIZED));
            doc.add(new Field(Fields.CONTENT_ID, contentId, Field.Store.YES, Field.Index.UN_TOKENIZED));
            doc.add(new Field(Fields.TITLE, title, Field.Store.YES, Field.Index.UN_TOKENIZED));
            doc.add(new Field(Fields.SITE_ID, siteId, Field.Store.YES, Field.Index.TOKENIZED));
            doc.add(new Field(Fields.CONTENT, content, Field.Store.NO, Field.Index.TOKENIZED));
            doc.add(new Field(Fields.CONTENT_UNSTEMMED, contentUnstemmed, Field.Store.NO, Field.Index.TOKENIZED));
            doc.add(new Field(Fields.KEYWORDS, keywords, Field.Store.NO, Field.Index.TOKENIZED));
            doc.add(new Field(Fields.LAST_MODIFIED, lastModified, Field.Store.YES, Field.Index.UN_TOKENIZED));
            doc.add(new Field(Fields.LANGUAGE, language, Field.Store.YES, Field.Index.UN_TOKENIZED));
            doc.add(new Field(Fields.CATEGORY, category, Field.Store.YES, Field.Index.TOKENIZED));
            doc.add(new Field(Fields.SUMMARY, summary, Field.Store.YES, Field.Index.UN_TOKENIZED));
            doc.add(new Field(Fields.TM_TOPICS, tmTopics, Field.Store.YES, Field.Index.TOKENIZED));
            doc.add(new Field(Fields.CONTENT_PARENTS, contentParents, Field.Store.YES, Field.Index.TOKENIZED));
            doc.add(new Field(Fields.CONTENT_TEMPLATE_ID, contentTemplateId, Field.Store.NO, Field.Index.UN_TOKENIZED));
            doc.add(new Field(Fields.CONTENT_STATUS, contentStatus, Field.Store.YES, Field.Index.UN_TOKENIZED));
            doc.add(new Field(Fields.CONTENT_VISIBILITY_STATUS, contentVisibilityStatus, Field.Store.YES, Field.Index.UN_TOKENIZED));
        }

        public Document getDocument() {
            return doc;
        }

    }

}
