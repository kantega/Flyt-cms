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

public enum ContentVisibilityStatus {
    WAITING(0),
    ACTIVE(10),
    ARCHIVED(15),
    EXPIRED(20);

    public final int statusId;

    ContentVisibilityStatus(int statusId) {
        this.statusId = statusId;
    }


    public static ContentVisibilityStatus fromId(int statusId) {
        if(statusId == WAITING.statusId){
            return WAITING;
        } else if (statusId == ACTIVE.statusId) {
            return ACTIVE;
        } else if (statusId == ARCHIVED.statusId) {
            return ARCHIVED;
        } else if (statusId == EXPIRED.statusId) {
            return EXPIRED;
        } else {
            throw new IllegalArgumentException("statusId should be one of 0, 10, 15, 20");
        }
    }

    public int getStatusId() {
        return statusId;
    }
}
