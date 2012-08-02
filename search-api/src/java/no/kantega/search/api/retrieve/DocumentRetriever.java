package no.kantega.search.api.retrieve;

/**
 * Retrieve the document which is the basis of the indexableDocument.
 * @param <D> - the class of documents the particular retriever handles.
 * @see no.kantega.search.api.provider.DocumentTransformer
 */
public interface DocumentRetriever<D> {
    /**
     * @return the indexableContentType this retriever handles.
     */
    public String getSupportedContentType();

    D getObjectById(int id);
}
