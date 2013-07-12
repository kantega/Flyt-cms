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

package no.kantega.publishing.modules.linkcheck.check;

/**
 *
 */
public interface CheckStatus {
    int OK = 1;
    int UNKNOWN_HOST = 2;
    int HTTP_NOT_200 = 3;
    int IO_EXCEPTION = 4;
    int CONNECTION_TIMEOUT = 5;
    int CIRCULAR_REDIRECT = 6;
    int CONNECT_EXCEPTION = 7;
    int CONTENT_AP_NOT_FOUND = 8;
    int INVALID_URL = 9;
    int ATTACHMENT_AP_NOT_FOUND = 10;
    int MULTIMEDIA_AP_NOT_FOUND = 11;
}
