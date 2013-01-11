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

package no.kantega.publishing.api.content;

public class ContentIdentifier {

    private int contentId = -1; // Peker til innhold - tilsvarer content.getId()
    private int associationId = -1; // Peker til menypunkt - tilsvarer association.getAssociationId()
    private int siteId = -1;    // Brukes sammen contentId for å finne en associationId
    private int contextId = -1; // Brukes sammen contentId for å finne en associationId
    private int language = Language.NORWEGIAN_BO;
    private int version = -1;
    private int status = -1;

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public int getAssociationId() {
        return associationId;
    }

    public void setAssociationId(int associationId) {
        this.associationId = associationId;
    }

    public void setContextId(int contextId) {
        this.contextId = contextId;
    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String toString() {
        String idStr;
        if (this.associationId != -1) {
            idStr = "thisId=" + associationId;
        } else {
            idStr = "contentId=" + contentId;
        }
        if (version != -1) {
            idStr += "&amp;version=" + version;
        }
        if (siteId != -1) {
            idStr += "&amp;siteId=" + siteId;
        }

        return idStr;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContentIdentifier that = (ContentIdentifier) o;

        if (associationId != that.associationId) return false;
        if (contentId != that.contentId) return false;
        if (contextId != that.contextId) return false;
        if (language != that.language) return false;
        if (siteId != that.siteId) return false;
        if (status != that.status) return false;
        if (version != that.version) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = contentId;
        result = 31 * result + associationId;
        result = 31 * result + siteId;
        result = 31 * result + contextId;
        result = 31 * result + language;
        result = 31 * result + version;
        result = 31 * result + status;
        return result;
    }


    public static ContentIdentifier fromAssociationId(int associationId) {
        ContentIdentifier contentIdentifier = new ContentIdentifier();
        contentIdentifier.setAssociationId(associationId);
        return contentIdentifier;
    }

    public static ContentIdentifier fromContentId(int contentId) {
        ContentIdentifier contentIdentifier =  new ContentIdentifier();
        contentIdentifier.setContentId(contentId);
        return contentIdentifier;
    }

    public int getContextId() {
        return contextId;
    }
}
