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

import no.kantega.commons.media.MimeType;
import no.kantega.commons.media.MimeTypes;
import no.kantega.publishing.api.content.Language;
import no.kantega.publishing.common.Aksess;

import java.util.Date;

public class Attachment {
    private static final String SOURCE = "aksess.Attachment";

    private int id = -1;
    private int contentId = -1;
    private int language = Language.NORWEGIAN_BO;

    private String filename = "";
    private Date lastModified = null;
    private int size = 0;

    private byte[] data = null;

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

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public MimeType getMimeType() {
        return MimeTypes.getMimeType(filename);
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
    
    public String getUrl(){
        String contextPath = Aksess.getContextPath();
        if (!contextPath.endsWith("/")) {
            contextPath += "/";
        }
        return contextPath + Aksess.ATTACHMENT_REQUEST_HANDLER + "?id=" + id;
    }
}
