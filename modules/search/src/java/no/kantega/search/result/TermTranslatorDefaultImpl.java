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

package no.kantega.search.result;

import no.kantega.commons.log.Log;
import no.kantega.search.index.Fields;
import no.kantega.search.index.IndexSearcherManager;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;

/**
 * Date: Jan 28, 2009
 * Time: 10:14:45 AM
 *
 * @author Tarje Killingberg
 */
public class TermTranslatorDefaultImpl implements TermTranslator {

    private static final String SOURCE = TermTranslatorDefaultImpl.class.getName();

    private IndexSearcherManager indexSearcherManager;


    public String fromField(String field) {
        return field;
    }

    public String fromTerm(String field, String term) {
        String retVal = null;

        if (Fields.CONTENT_PARENTS.equals(field)) {
            retVal = lookupContentTitle(term);
        }

        return retVal != null ? retVal : term;
    }

    public void setIndexSearcherManager(IndexSearcherManager indexSearcherManager) {
        this.indexSearcherManager = indexSearcherManager;
    }

    private String lookupContentTitle(String term) {
        String retVal = null;
        try {
            Query query = new TermQuery(new Term(Fields.CONTENT_ID, term));
            IndexSearcher searcher = indexSearcherManager.getSearcher("aksess");
            TopDocs topDocs = searcher.search(query, 1);
            if (topDocs.scoreDocs.length > 0) {
                retVal = searcher.doc(topDocs.scoreDocs[0].doc).get(Fields.TITLE);
            }
        } catch (IOException e) {
            Log.error(SOURCE, e, "fromTerm", null);
        }
        return retVal;
    }

}
