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

public interface DocumentProvider {
    public String getSourceId();
    public String getDocumentType();
    public void provideDocuments(DocumentProviderHandler handler, ProgressReporter reporter);
    public Document provideDocument(String id);
    public Term getDeleteTerm(String id);
    public Term getDeleteAllTerm();
    public SearchHit createSearchHit();
    public void processSearchHit(SearchHit searchHit, SearchHitContext searchHitContext, Document doc) throws NotAuthorizedException;
}
