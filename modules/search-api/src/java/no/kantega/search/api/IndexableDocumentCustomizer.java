package no.kantega.search.api;

/**
 * By implementing this interface it is possible to modify the IndexableDocument before it is sent to the indexer
 * Should be used in either DocumentTransformer after normal transform is done, or in IndexableDocumentProvider after
 * DocumentTransformer has returned.
 * The prefered way is that your DocumentTransformer extends DocumentTransformerAdapter and get a reference to the
 * indexableDocumentCustomizers via DocumentTransformerAdapter.getIndexableDocumentCustomizers().
 * @param <T> - the type of content to process, e.g. If the type is Content ContentTransformer is the DocumentTransformer
 *           that will use the customizer.
 */
public interface IndexableDocumentCustomizer<T> {
    public Class<T> typeHandled();

    /**
     * After the Transformer for T-type content is finished it will run through all <code>IndexableDocumentCustomizer</code>s
     * and send the result returned from these to the index.
     * @param originalObject - The object that the indexableDocument is based upon.
     * @param indexableDocument - The IndexableDocument that has already been built by the DocumentTransformer to T
     * @return the modified IndexableDocument.
     */
    public IndexableDocument customizeIndexableDocument(T originalObject, IndexableDocument indexableDocument);
}
