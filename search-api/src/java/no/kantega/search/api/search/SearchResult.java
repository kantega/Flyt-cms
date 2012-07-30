package no.kantega.search.api.search;

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
    private Object document;
    private final int securityId;

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

    public Object getDocument() {
        return document;
    }

    public void setDocument(Object document) {
        this.document = document;
    }

    public int getSecurityId() {
        return securityId;
    }
}
