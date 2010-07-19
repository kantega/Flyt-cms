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

package no.kantega.search.index;

import no.kantega.search.analysis.AnalyzerFactory;
import no.kantega.search.index.jobs.IndexJob;
import no.kantega.search.index.jobs.context.JobContext;
import no.kantega.search.index.provider.DocumentProviderSelector;

/**
 * Date: Jan 5, 2009
 * Time: 11:22:42 AM
 *
 * @author Tarje Killingberg
 */
public class IndexManagerImpl implements IndexManager {

    private static final String SOURCE = IndexManagerImpl.class.getName();
    private static IndexManagerImpl instance;

    private IndexWriterManager indexWriterManager;
    private IndexReaderManager indexReaderManager;
    private IndexSearcherManager indexSearcherManager;
    private IndexJobManager indexJobManager;
    private DocumentProviderSelector documentProviderSelector;
    private AnalyzerFactory analyzerFactory;


    public IndexManagerImpl() {
        instance = this;
    }

    public static IndexManagerImpl getInstance() {
        if (instance == null) {
            instance = new IndexManagerImpl();
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    public void addIndexJob(IndexJob job) {
        indexJobManager.addIndexJob(job);
    }

    public void setIndexJobManager(IndexJobManager indexJobManager) {
        this.indexJobManager = indexJobManager;
    }

    /**
     * {@inheritDoc}
     */
    public IndexReaderManager getIndexReaderManager() {
        return indexReaderManager;
    }

    public void setIndexReaderManager(IndexReaderManager indexReaderManager) {
        this.indexReaderManager = indexReaderManager;
    }

    /**
     * {@inheritDoc}
     */
    public IndexSearcherManager getIndexSearcherManager() {
        return indexSearcherManager;
    }

    public void setIndexSearcherManager(IndexSearcherManager indexSearcherManager) {
        this.indexSearcherManager = indexSearcherManager;
    }

    /**
     * {@inheritDoc}
     */
    public IndexWriterManager getIndexWriterManager() {
        return indexWriterManager;
    }

    public void setIndexWriterManager(IndexWriterManager indexWriterManager) {
        this.indexWriterManager = indexWriterManager;
    }

    /**
     * {@inheritDoc}
     */
    public DocumentProviderSelector getDocumentProviderSelector() {
        return documentProviderSelector;
    }

    public void setDocumentProviderSelector(DocumentProviderSelector documentProviderSelector) {
        this.documentProviderSelector = documentProviderSelector;
    }

    private DocumentProviderSelector findDocumentProviderSelector() {
        return documentProviderSelector;
    }

    /**
     * {@inheritDoc}
     */
    public AnalyzerFactory getAnalyzerFactory() {
        return analyzerFactory;
    }

    public void setAnalyzerFactory(AnalyzerFactory analyzerFactory) {
        this.analyzerFactory = analyzerFactory;
    }


    class DefaultJobContext implements JobContext {


        public IndexWriterManager getIndexWriterManager() {
            return indexWriterManager;
        }

        public IndexReaderManager getIndexReaderManager() {
            return indexReaderManager;
        }

        public DocumentProviderSelector getDocumentProviderSelector() {
            return findDocumentProviderSelector();
        }

        public boolean isStopRequested() {
            return indexJobManager.getShutdownHint();
        }

    }

}
