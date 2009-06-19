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
    public static final int WAITING = 0;    // Venter på godkjenning
    public static final int REJECTED = 5;   // Godkjenning avslått
    public static final int ARCHIVED = 10;  // Arkivert
    public static final int DRAFT = 20;     // Kladd, ikke publisert enda
    public static final int PUBLISHED = 30; // Publisert
    public static final int HEARING = 40;   // Sendt til høring
}
