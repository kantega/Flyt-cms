package no.kantega.search.api;

/**
 * Adapter with empty default implementations of methods in IndexableDocumentCustomizer
 * @param <T>
 */
public abstract class IndexableDocumentCustomizerAdapter<T> implements IndexableDocumentCustomizer<T> {

    private final Class<T> typeHandled;

    protected IndexableDocumentCustomizerAdapter(Class<T> typeHandled) {
        this.typeHandled = typeHandled;
    }

    @Override
    public Class<T> typeHandled() {
        return typeHandled;
    }

    @Override
    public IndexableDocument customizeIndexableDocument(T originalObject, IndexableDocument indexableDocument) {
        return indexableDocument;
    }
}
