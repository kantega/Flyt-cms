package no.kantega.search.api.provider;

import no.kantega.search.api.IndexableDocument;
import no.kantega.search.api.IndexableDocumentCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.util.Collections.emptyList;

/**
 * Adapter implementation that only returns null and has a field for indexableDocumentCustomizers
 * @param <D>
 */
public abstract class DocumentTransformerAdapter<D> implements DocumentTransformer<D> {

    private final Logger log = LoggerFactory.getLogger(DocumentTransformerAdapter.class);
    private List<IndexableDocumentCustomizer<D>> indexableDocumentCustomizers = emptyList();


    private final Class<D> typeHandled;

    protected DocumentTransformerAdapter(Class<D> typeHandled) {
        this.typeHandled = typeHandled;
    }

    @Override
    public Class<D> typeHandled() {
        return typeHandled;
    }

    /**
     * Should be overridden!
     * @param document of type D
     * @return default null.
     */
    @Override
    public IndexableDocument transform(D document) {
        log.warn("DocumentTransformerAdapter.transform was called, should be overridden");
        return null;
    }

    /**
     * Should be overridden!
     * @param document of type D
     * @return default null.
     */
    @Override
    public String generateUniqueID(D document) {
        log.warn("DocumentTransformerAdapter.generateUniqueID was called, should be overridden");
        return null;
    }

    @Override
    public void setIndexableDocumentCustomizers(List<IndexableDocumentCustomizer<D>> indexableDocumentCustomizers) {
        this.indexableDocumentCustomizers = indexableDocumentCustomizers;
    }

    public List<IndexableDocumentCustomizer<D>> getIndexableDocumentCustomizers() {
        return indexableDocumentCustomizers;
    }
}
