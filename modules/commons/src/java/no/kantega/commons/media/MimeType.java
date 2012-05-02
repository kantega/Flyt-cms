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
    private final String fileExtension;
    private final String type;
    private final String description;
    private final boolean dimensionRequired;
    private final boolean userMustInputDimension;

    /**
     * @param fileExtension of the mime type, e.g. "png"
     * @param type the mime type string, eg. "image/png"
     * @param desc a human understandable description of the type, e.g. "PNG bilde"
     */
    public MimeType(String fileExtension, String type, String desc) {
        this.fileExtension = fileExtension;
        this.type = type;
        this.description = desc;

        dimensionRequired = (type.contains("video") || type.contains("image") || type.contains("flash")) && !type.equals("video/x-flv");
        userMustInputDimension = type.contains("video") && !type.equals("video/x-flv");
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDimensionRequired() {
        return dimensionRequired;
    }

    public boolean userMustInputDimension() {
        return userMustInputDimension;
    }

    @Override
    public String toString() {
        return type;
    }
}
