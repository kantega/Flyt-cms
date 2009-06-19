/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.search.model;

import org.apache.lucene.document.Document;

import java.util.List;
import java.util.Date;

import no.kantega.search.result.SearchHit;
import no.kantega.publishing.common.data.PathEntry;

public class AksessSearchHit implements SearchHit {

    private Document document = null;
    private String title = "";
    private String summary = "";
    private String allText = "";
    private String contextText = "";
    private String url = "";
    private Date lastModified = null;
    private List<PathEntry> pathElements = null;
    private String fileExtension = null;
    private boolean doOpenInNewWindow = false;


    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public List<PathEntry> getPathElements() {
        return pathElements;
    }

    public void setPathElements(List<PathEntry> pathElements) {
        this.pathElements = pathElements;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getAllText() {
        return allText;
    }

    public void setAllText(String allText) {
        this.allText = allText;
    }

    public String getContextText() {
        return contextText;
    }

    public void setContextText(String contextText) {
        this.contextText = contextText;
    }

    public boolean isDoOpenInNewWindow() {
        return doOpenInNewWindow;
    }

    public void setDoOpenInNewWindow(boolean openInNewWindow) {
        this.doOpenInNewWindow = openInNewWindow;
    }
}
