package no.kantega.search.api.provider;

import no.kantega.search.api.IndexableDocument;
import no.kantega.search.api.IndexableDocumentCustomizer;

import java.util.List;

/**
 * Transform a document of type <D> into an IndexableDocument
 * @param <D> - the class of documents the particular transformer handles.
 */
public interface DocumentTransformer<D> {

    /**
     * @return Class of D. Needed to find the correct IndexableDocumentCustomizers.
     */
    public Class<D> typeHandled();

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

    /**
     * Allow projects using Flyt CMS to customize the IndexableDocuments created by for instance ContentTransformer
     * @param indexableDocumentCustomizers that handles D.
     */
    public void setIndexableDocumentCustomizers(List<IndexableDocumentCustomizer<D>> indexableDocumentCustomizers);
}
