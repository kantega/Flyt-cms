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

import java.io.Serializable;
import java.util.Date;

import static no.kantega.publishing.common.util.PrettyURLEncoder.encode;

public class Attachment implements Serializable {

    private int id = -1;
    private int contentId = -1;
    private int language = Language.NORWEGIAN_BO;

    private String filename = "";
    private Date lastModified = null;
    private int size = 0;
    private boolean isSearchable = true;

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
        return getFilenameWithSuffixLowerCase();
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

    public String getUrl() {
        String contextPath = Aksess.getContextPath();
        if (contextPath.endsWith("/")) {
            contextPath = contextPath.substring(0, contextPath.length() - 1);
        }
        return contextPath + Aksess.ATTACHMENT_URL_PREFIX + "/" + id + "/" + encode(getFilenameWithSuffixLowerCase());
    }

    private String getFilenameWithSuffixLowerCase() {
        int inx = filename.lastIndexOf('.');
        if(inx != -1){
            int length = filename.length();
            String extension = filename.substring(inx, length).toLowerCase();
            return filename.substring(0, inx) +  extension;
        }else {
            return filename;
        }
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "contentId=" + contentId +
                ", id=" + id +
                ", filename='" + filename + '\'' +
                ", size=" + size +
                '}';
    }

    public boolean isSearchable() {
        return isSearchable;
    }

    public Attachment setSearchable(boolean searchable) {
        isSearchable = searchable;
        return this;
    }
}
