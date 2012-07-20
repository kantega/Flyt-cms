package no.kantega.search.api;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class IndexableDocument {
    private final String uid;
    private String id;
    private String contentType;
    private String title;
    private String description;
    private String contentStatus;
    private String visibility;
    private int siteId;
    private String language;
    private File fileContent;
    private Map<String, Object> attributes = new HashMap<String, Object>();
    private boolean shouldIndex = false;

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
}