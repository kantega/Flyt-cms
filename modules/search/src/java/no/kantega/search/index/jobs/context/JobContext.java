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

package no.kantega.search.index.jobs.context;

import no.kantega.search.index.IndexReaderManager;
import no.kantega.search.index.IndexWriterManager;
import no.kantega.search.index.provider.DocumentProviderSelector;

/**
 * A bean holding references to the searchcontext.
 * I.e. for reading and writing to the index, as well
 * as the different methods of accessing the providers that convert
 * content do Lucene Documents.
 */
public interface JobContext {


    public IndexWriterManager getIndexWriterManager();
    public IndexReaderManager getIndexReaderManager();
    public DocumentProviderSelector getDocumentProviderSelector();
    public boolean isStopRequested();

}
