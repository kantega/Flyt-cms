package no.kantega.search.api.provider;

import no.kantega.search.api.IndexableDocument;
import no.kantega.search.api.index.ProgressReporter;

import java.util.concurrent.BlockingQueue;

public interface IndexableDocumentProvider {

    public ProgressReporter provideDocuments(BlockingQueue<IndexableDocument> indexableDocumentQueue, int numberOfThreadsToUse);

}
