package no.kantega.openaksess.search.provider.result;

import no.kantega.publishing.api.path.PathEntry;
import no.kantega.search.api.search.SearchResult;

import java.util.List;

public class OASearchResult extends SearchResult {
    private final int associationId;
    private final String author;
    private final List<PathEntry> pathEntries;

    /**
     * @param id                 of the content this result is based on.
     * @param securityId         of the content this result is based on.
     * @param indexedContentType the type on content this is.
     * @param title              of the document.
     * @param description        of the document.
     * @param author             of the document.
     * @param url                of the document.
     * @param parentId           - associationId of the parent of this result.
     * @param pathEntries        of the anchestors.
     */
    public OASearchResult(int id, int associationId, int securityId, String indexedContentType, String title,
                          String description, String author, String url, int parentId, List<PathEntry> pathEntries) {
        super(id, securityId, indexedContentType, title, description, url, parentId);
        this.author = author;
        this.pathEntries = pathEntries;
        this.associationId = associationId;
    }

    public String getAuthor() {
        return author;
    }

    public List<PathEntry> getPathEntries() {
        return pathEntries;
    }

    public int getAssociationId() {
        return associationId;
    }
}
