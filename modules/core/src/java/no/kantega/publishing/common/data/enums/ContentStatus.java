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

package no.kantega.publishing.common.data.enums;

public class ContentStatus {
    public static final int WAITING_FOR_APPROVAL = 0;
    public static final int REJECTED = 5;   // Rejected by editor
    public static final int ARCHIVED = 10;
    public static final int DRAFT = 20;
    public static final int PUBLISHED_WAITING = 25; // Waiting to become PUBLISHED
    public static final int PUBLISHED = 30;
    public static final int HEARING = 40; // Sent to hearing

    public static String getContentStatusAsString(int contentStatus) {
        switch (contentStatus){
            case ContentStatus.ARCHIVED:
                return "ARCHIVED";
            case ContentStatus.DRAFT:
                return "DRAFT";
            case ContentStatus.HEARING:
                return "HEARING";
            case ContentStatus.PUBLISHED:
                return "PUBLISHED";
            case ContentStatus.PUBLISHED_WAITING:
                return "PUBLISHED_WAITING";
            case ContentStatus.REJECTED:
                return "REJECTED";
            case ContentStatus.WAITING_FOR_APPROVAL:
                return "WAITING_FOR_APPROVAL";
            default:
                return "UNKNOWN";
        }
    }
}
