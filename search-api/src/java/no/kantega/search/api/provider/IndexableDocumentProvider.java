package no.kantega.search.api.provider;

import no.kantega.search.api.IndexableDocument;

import java.util.Iterator;

public interface IndexableDocumentProvider {

    public Iterator<IndexableDocument> provideDocuments();

    public long getNumberOfDocuments();

}
