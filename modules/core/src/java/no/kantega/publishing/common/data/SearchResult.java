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

package no.kantega.publishing.common.data;

import no.kantega.publishing.common.data.enums.MultimediaType;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.util.PrettyURLEncoder;

import java.util.Date;

public class SearchResult {
    private static final String SOURCE = "aksess.SearchResult";

    private int contentId;
    private int status;
    private String title;
    private String description;
    private Date lastModified;

    public SearchResult(int contentId, int status, String title, String description, Date lastModified) {
        this.contentId = contentId;
        this.status = status;
        this.title = title;
        this.description = description;
        this.lastModified = lastModified;
    }

    public int getContentId() {
        return contentId;
    }

    public int getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public String getUrl() {
        return Aksess.getContextPath() + PrettyURLEncoder.createContentUrl(contentId, title);
    }
}
