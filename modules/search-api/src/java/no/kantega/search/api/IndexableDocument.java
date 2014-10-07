package no.kantega.search.api;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * A document containing information to be indexed.
 * If fields other than the ones specified should be indexed they should be put
 * into the attributes map. All primitive objects and Date are handled automatically.
 * The names of the attributes must have a postfix such that it is picked up by the dynamic fields in solr, defined
 * in schema.xml.
 *
 * If content in a file is to be submitted for indexing, save it to a file and use setFileContent. The content of the
 * file is then associated with this document.
 */
public class IndexableDocument {
    private final String uid;
    private String id;
    private String contentType;
    private String title;
    private String description;
    private String contentStatus;
    private String visibility;
    private int siteId = -1;
    private String language;
    private File fileContent;
    private Map<String, Object> attributes = new HashMap<>();
    private boolean shouldIndex = false;
    private int securityId;
    private int parentId;

    /**
     * @param uid unique for the whole index. Typically indexedContentType-Id.
     */
    public IndexableDocument(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContentStatus() {
        return contentStatus;
    }

    public void setContentStatus(String contentStatus) {
        this.contentStatus = contentStatus;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void addAttribute(String name, Object value) {
        this.attributes.put(name, value);
    }

    public File getFileContent() {
        return fileContent;
    }

    public void setFileContent(File fileContent) {
        this.fileContent = fileContent;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getUId() {
        return uid;
    }

    public boolean shouldIndex() {
        return shouldIndex;
    }

    public void setShouldIndex(boolean shouldIndex) {
        this.shouldIndex = shouldIndex;
    }

    public void setSecurityId(int securityId) {
        this.securityId = securityId;
    }

    public int getSecurityId() {
        return securityId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getParentId() {
        return parentId;
    }
}