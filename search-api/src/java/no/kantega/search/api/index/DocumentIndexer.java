package no.kantega.search.api.index;

import no.kantega.search.api.IndexableDocument;

import java.util.List;

public interface DocumentIndexer {
    public void indexDocument(IndexableDocument document);
    public void indexDocumentAndCommit(IndexableDocument document);

    public void commit();

    public void deleteById(List<String> s);

    public void optimize();
}
