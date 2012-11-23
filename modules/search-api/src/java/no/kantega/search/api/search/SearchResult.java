package no.kantega.search.api.search;

/**
 * A single hit in a SearchResponse
 * @see SearchResponse
 */
public class SearchResult {
    /*
    private String title = "";
    private String summary = "";
    private String allText = "";
    private String contextText = "";
    private String url = "";
    private Date lastModified = null;
    private List<PathEntry> pathElements = null;
    private String fileExtension = null;
    private String fileName = null;
    private int fileSize = 0;
    private MimeType mimeType = null;
    private boolean doOpenInNewWindow = false;
    private int id = -1;
    private Content contentObject;
     */


    private final int id;
    private final String indexedContentType;
    private final String title;
    private final String description;
    private final String author;
    private final String url;
    private final int securityId;

    /**
     * @param id of the content this result is based on.
     * @param securityId of the content this result is based on.
     * @param indexedContentType the type on content this is.
     * @param title of the document.
     * @param description of the document.
     * @param author of the document.
     * @param url of the document.
     */
    public SearchResult(int id, int securityId, String indexedContentType, String title, String description, String author, String url) {
        this.id = id;
        this.securityId = securityId;
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

    public int getSecurityId() {
        return securityId;
    }
}
