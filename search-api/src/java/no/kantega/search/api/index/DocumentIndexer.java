package no.kantega.search.api.index;

import no.kantega.search.api.IndexableDocument;

public interface DocumentIndexer {
    public void indexDocument(IndexableDocument document);

    void commit();
}
