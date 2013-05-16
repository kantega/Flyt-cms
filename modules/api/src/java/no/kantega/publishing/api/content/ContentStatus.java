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

public enum ContentStatus {
    /**
     *Content is waiting for approval from editor.
     */
    WAITING_FOR_APPROVAL(0),
    /**
     *Content is rejected by editor.
     */
    REJECTED(5),
    /**
     *Content has been rejected.
     */
    ARCHIVED(10),
    /**
     * Content is saved as draft.
     */
    DRAFT(20),
    /**
     * Content is waiting to become PUBLISHED.
     */
    PUBLISHED_WAITING(25),
    /**
     * Content is published.
     */
    PUBLISHED(30),
    /**
     * Content is sent to hearing.
     */
    HEARING(40);

    private final int statusAsInt;

    ContentStatus(int statusAsInt) {
        this.statusAsInt = statusAsInt;
    }

    public int getTypeAsInt() {
        return statusAsInt;
    }

    public static ContentStatus getContentStatusAsEnum(int typeAsInt) {
        for (ContentStatus type : ContentStatus.values()) {
            if (type.getTypeAsInt() == typeAsInt) {
                return type;
            }
        }

        return null;
    }
}