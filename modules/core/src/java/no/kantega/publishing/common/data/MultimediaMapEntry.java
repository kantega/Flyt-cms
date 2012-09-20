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
import no.kantega.publishing.common.data.enums.ObjectType;
import no.kantega.publishing.common.data.enums.MultimediaType;

public class MultimediaMapEntry extends NavigationMapEntry {
    public MultimediaType type = MultimediaType.MEDIA;
    private String filename = "";

    public MultimediaMapEntry (int currentId, int parentId, MultimediaType type, String title) {
        this.currentId = currentId;
        this.parentId  = parentId;
        this.type = type;
        this.title = title;
    }

    public String getUrl() {
        return "";
    }

    public int getObjectType() {
        return ObjectType.MULTIMEDIA;
    }

    public String getName() {
        return title;
    }
        
    public String getOwner() {
        return null;
    }

    public String getOwnerPerson() {
        return null;
    }

    public MultimediaType getType() {
        return type;
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

}
