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

package no.kantega.publishing.security.data.enums;

/**
 * User: Anders Skar, Kantega AS
 * Date: Jan 16, 2009
 * Time: 9:55:04 AM
 */
public enum NotificationPriority {
    PRIORITY1(1),
    PRIORITY2(2),
    PRIORITY3(3),
    NO_PRIORITY(0);

    private int priorityAsInt;

    NotificationPriority(int priorityAsInt) {
        this.priorityAsInt = priorityAsInt;
    }

    public int getNotificationPriorityAsInt() {
        return priorityAsInt;
    }

    public static NotificationPriority getNotificationPriorityAsEnum(int priorityAsInt) {
        for (NotificationPriority type : NotificationPriority.values()) {
            if (type.getNotificationPriorityAsInt() == priorityAsInt) {
                return type;
            }
        }

        return null;
    }

    public String toString() {
        return "" + getNotificationPriorityAsInt();
    }
}
