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

package no.kantega.publishing.modules.linkcheck.check;

import java.util.Date;

public class LinkOccurrence {
    private int id;
    private int contentId;
    private String contentTitle;
    private String attributeName;
    private int linkId;
    private Date firstFound;
    private Date lastChecked;
    private CheckStatus status;
    private int httpStatus;
    private String url;
    private int timesChecked;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public int getLinkId() {
        return linkId;
    }

    public void setLinkId(int linkId) {
        this.linkId = linkId;
    }

    public Date getLastChecked() {
        return lastChecked;
    }

    public void setLastChecked(Date lastChecked) {
        this.lastChecked = lastChecked;
    }

    public CheckStatus getStatus() {
        return status;
    }

    public void setStatus(CheckStatus status) {
        this.status = status;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public Date getFirstFound() {
        return firstFound;
    }

    public void setFirstFound(Date firstFound) {
        this.firstFound = firstFound;
    }

    public int getTimesChecked() {
        return timesChecked;
    }

    public void setTimesChecked(int timesChecked) {
        this.timesChecked = timesChecked;
    }
}
