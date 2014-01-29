package no.kantega.search.api.provider;

import no.kantega.search.api.IndexableDocument;

/**
 * Transform a document of type <D> into an IndexableDocument
 * @param <D> - the class of documents the particular transformer handles.
 * @see no.kantega.search.api.retrieve.DocumentRetriever
 */
public interface DocumentTransformer<D> {
    /**
     * @param document of type D
     * @return the document represented as an IndexableDocument
     */
    public IndexableDocument transform(D document);

    /**
     * @param document of type D
     * @return the unique ID which will be given to this document when transformed.
     */
    public String generateUniqueID(D document);
}
