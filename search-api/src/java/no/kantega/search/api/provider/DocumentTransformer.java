package no.kantega.search.api.provider;

import no.kantega.search.api.IndexableDocument;

public interface DocumentTransformer<D> {
    public IndexableDocument transform(D document);

    public String getSupportedContentType();

    public String generateUniqueID(D document);
}
