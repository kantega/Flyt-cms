package no.kantega.search.api.search;

public class SearchResult {
    private final int id;
    private final String indexedContentType;
    private final String title;
    private final String description;
    private final String author;
    private final String url;

    public SearchResult(int id, String indexedContentType, String title, String description, String author, String url) {
        this.id = id;
        this.indexedContentType = indexedContentType;
        this.title = title;
        this.description = description;
        this.author = author;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public String getIndexedContentType() {
        return indexedContentType;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public String getUrl() {
        return url;
    }
}
