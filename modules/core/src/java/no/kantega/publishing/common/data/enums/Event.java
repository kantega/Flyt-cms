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

public class Event {
    public static final String PUBLISH_CONTENT = "PUBLISH_CONTENT";
    public static final String MOVE_CONTENT = "MOVE_CONTENT";
    public static final String SAVE_DRAFT = "SAVE_DRAFT";
    public static final String SEND_FOR_APPROVAL = "SEND_FOR_APPROVAL";
    public static final String DELETE_CONTENT = "DELETE_CONTENT";
    public static final String DELETE_CONTENT_TRASH = "DELETE_CONTENT_TRASH";
    public static final String DELETE_CONTENT_EXPIRE = "DELETE_CONTENT_EXPIRE";
    public static final String DELETE_CONTENT_VERSION = "DELETE_CONTENT_VERSION";
    public static final String APPROVED = "APPROVED";
    public static final String REJECTED = "REJECTED";

    public static final String DELETE_ATTACHMENT = "DELETE_ATTACHMENT";
    public static final String SAVE_ATTACHMENT = "SAVE_ATTACHMENT";

    public static final String DELETE_MULTIMEDIA = "DELETE_MULTIMEDIA";
    public static final String SAVE_MULTIMEDIA = "SAVE_MULTIMEDIA";

    public static final String DELETE_TOPIC = "DELETE_TOPIC";
    public static final String SAVE_TOPIC = "SAVE_TOPIC";

    public static final String SAVE_SITE = "SAVE_SITE";

    public static final String SET_PERMISSIONS = "SET_PERMISSIONS";

    public static final String FAILED_LOGIN = "FAILED_LOGIN";

    public static final String[] ALL_EVENTS = {PUBLISH_CONTENT, MOVE_CONTENT, SAVE_DRAFT, SEND_FOR_APPROVAL, DELETE_CONTENT, DELETE_CONTENT_TRASH, DELETE_CONTENT_EXPIRE, DELETE_CONTENT_VERSION, DELETE_ATTACHMENT,
    APPROVED, REJECTED, SAVE_ATTACHMENT, DELETE_TOPIC, SAVE_TOPIC, DELETE_MULTIMEDIA, SAVE_MULTIMEDIA, SAVE_SITE, SET_PERMISSIONS, FAILED_LOGIN};
}
