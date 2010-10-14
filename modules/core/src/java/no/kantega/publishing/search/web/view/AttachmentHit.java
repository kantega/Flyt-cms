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

package no.kantega.publishing.search.web.view;


public class AttachmentHit extends Hit {

    private String fileName;

    private String fileSize;

    public String getType() {
        return "attachment";
    }

    public String getSuffix() {

        if(getFileName() != null && getFileName().indexOf(".") >= 0) {
            return getFileName().substring(getFileName().lastIndexOf(".") +(getFileName().endsWith(".") ? 0 : 1));
        } else {
            return "";
        }

    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

}
