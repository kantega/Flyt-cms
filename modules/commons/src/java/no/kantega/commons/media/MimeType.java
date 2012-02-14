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

package no.kantega.commons.media;

public class MimeType {
    private String fileExtension = null;
    private String type = null;
    private String description = null;
    private boolean dimensionRequired = false;
    private boolean userMustInputDimension = false;
    private boolean shouldConvertImage = false;

    public MimeType(String fileExtension, String type, String desc) {
        this(fileExtension, type, desc, false);
    }

    public MimeType(String fileExtension, String type, String desc,  boolean shouldConvertImage) {
        this.fileExtension = fileExtension;
        this.type = type;
        this.description = desc;
        if (type.contains("video") || type.contains("image") || type.contains("flash")) {
            if (!type.equals("video/x-flv")) {
                dimensionRequired = true;
                if (type.contains("video")) {
                    userMustInputDimension = true;
                }
            }
        }
        this.shouldConvertImage = shouldConvertImage;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public boolean isDimensionRequired() {
        return dimensionRequired;
    }

    public void setDimensionRequired(boolean dimensionRequired) {
        this.dimensionRequired = dimensionRequired;
    }

    public boolean userMustInputDimension() {
        return userMustInputDimension;
    }

    public boolean shouldConvertImage() {
        return shouldConvertImage;
    }

    @Override
    public String toString() {
        return type;
    }
}
