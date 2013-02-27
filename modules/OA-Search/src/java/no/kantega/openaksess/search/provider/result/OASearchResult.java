package no.kantega.openaksess.search.provider.result;

import no.kantega.publishing.api.path.PathEntry;
import no.kantega.search.api.search.SearchResult;

import java.util.List;

public class OASearchResult extends SearchResult {
    /**
     * @param id                 of the content this result is based on.
     * @param securityId         of the content this result is based on.
     * @param indexedContentType the type on content this is.
     * @param title              of the document.
     * @param description        of the document.
     * @param author             of the document.
     * @param url                of the document.
     * @param parentId           - associationId of the parent of this result.
     * @param pathEntriesByAssociationIdInclusive
     */
    public OASearchResult(int id, int securityId, String indexedContentType, String title, String description, String author, String url, int parentId, List<PathEntry> pathEntriesByAssociationIdInclusive) {
        super(id, securityId, indexedContentType, title, description, author, url, parentId);
    }
}
