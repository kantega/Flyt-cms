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

public class Privilege {
    // Privilegier med høyere verdi inkluderer implisitt privilegier med lavere verdi
    public static final int VIEW_CONTENT = 0;
    public static final int UPDATE_CONTENT = 1;
    public static final int APPROVE_CONTENT = 2;
    public static final int FULL_CONTROL = 3;
}
