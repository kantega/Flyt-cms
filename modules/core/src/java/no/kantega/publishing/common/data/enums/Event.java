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
    public static final String PUBLISH_CONTENT = "Publisert innhold";
    public static final String MOVE_CONTENT = "Flyttet innhold";
    public static final String SAVE_DRAFT = "Lagret kladd";
    public static final String SEND_FOR_APPROVAL = "Sendt til godkjenning";
    public static final String DELETE_CONTENT = "Slettet innhold";
    public static final String DELETE_CONTENT_TRASH = "Slettet fra søppelkurv";
    public static final String DELETE_CONTENT_EXPIRE = "Slettet - utløpt på dato";
    public static final String DELETE_CONTENT_VERSION = "Slettet innholdsversjon";
    public static final String APPROVED = "Godkjent";
    public static final String REJECTED = "Forkastet";

    public static final String DELETE_ATTACHMENT = "Slettet vedlegg";
    public static final String SAVE_ATTACHMENT = "Lagret vedlegg";

    public static final String DELETE_MULTIMEDIA = "Slettet multimediaobjekt";
    public static final String SAVE_MULTIMEDIA = "Lagret multimediaobjekt";

    public static final String DELETE_TOPIC = "Slettet emne";
    public static final String SAVE_TOPIC = "Lagret emne";

    public static final String SAVE_SITE = "Lagret nettsted";

    public static final String SET_PERMISSIONS = "Endret rettigeter";

    public static final String FAILED_LOGIN = "Feilet pålogging";

    public static final String[] ALL_EVENTS = {PUBLISH_CONTENT, MOVE_CONTENT, SAVE_DRAFT, SEND_FOR_APPROVAL, DELETE_CONTENT, DELETE_CONTENT_TRASH, DELETE_CONTENT_EXPIRE, DELETE_CONTENT_VERSION, DELETE_ATTACHMENT,
    APPROVED, REJECTED, SAVE_ATTACHMENT, DELETE_TOPIC, SAVE_TOPIC, DELETE_MULTIMEDIA, SAVE_MULTIMEDIA, SAVE_SITE, SET_PERMISSIONS, FAILED_LOGIN};
}
