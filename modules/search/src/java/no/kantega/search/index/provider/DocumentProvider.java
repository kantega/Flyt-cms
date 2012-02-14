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

package no.kantega.search.index.provider;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.search.index.rebuild.ProgressReporter;
import no.kantega.search.result.SearchHit;
import no.kantega.search.result.SearchHitContext;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;

import java.util.Map;

/**
 * A DocumentProvider converts some type of content into a
 * Lucene Document.
 */
public interface DocumentProvider {
    public String getSourceId();

    /**
     * @return The type of document this provider provides
     */
    public String getDocumentType();
    public void provideDocuments(DocumentProviderHandler handler, ProgressReporter reporter);

    /**
     * Goes through all content this provider is responsible for, create Lucene documents for them
     * and hand them over to the handler in order to be persisted in the index.
     * @param handler responsible for persisting the documents given to it.
     * @param reporter for reporting how many of the total number of documents has been processed.
     * @param options to the DocumentProvider e.g. how many concurrent index workers should be used.
     */
    public void provideDocuments(DocumentProviderHandler handler, ProgressReporter reporter, Map options);

    /**
     * @param id of the to be indexed document
     * @return a Lucene document representing the document with the given id.
     */
    public Document provideDocument(String id);

    /**
     * @param id for the document we want to remove from the index.
     * @return a Term identifying the document with the given id in the index.
     */
    public Term getDeleteTerm(String id);

    /**
     * @return a term identifying all documents provided by this DocumentProvider in the index.
     */
    public Term getDeleteAllTerm();
    public SearchHit createSearchHit();

    /**
     * Populates the searchHit with data from Document.
     * @param searchHit we want populated
     * @param searchHitContext representing the current search context
     * @param doc the lucene hit document we want to expose
     * @throws NotAuthorizedException if the user in the searchContext does not have sufficient privilegies to
     * read this particular document.
     */
    public void processSearchHit(SearchHit searchHit, SearchHitContext searchHitContext, Document doc) throws NotAuthorizedException;
}
