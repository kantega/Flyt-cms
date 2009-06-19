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

import no.kantega.publishing.api.model.PublicIdObject;

import java.util.Date;

/**
 *
 */
public class AssociationCategory implements PublicIdObject {
    private String name = null;
    private String description = null;
    private Date lastModified;

    private int id = -1;
    private String publicId = "";

    public AssociationCategory() {
    }

    public AssociationCategory(int id) {
        this.id = id;
    }

    /**
     * @deprecated
     */
    public AssociationCategory(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null) {
            description = "";
        }
        this.description = description;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String toString() {
        String str = "";

        if (publicId != null && publicId.length() > 0) {
            str = publicId;
        }

        if (id != -1) {
            str = str + "(" + id + ")";
        }

        return str;
    }
}
