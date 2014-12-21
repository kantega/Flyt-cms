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
 * Status for checked url
 */
public enum CheckStatus {

    OK(1),
    UNKNOWN_HOST(2),
    HTTP_NOT_200(3),
    IO_EXCEPTION(4),
    CONNECTION_TIMEOUT(5),
    CIRCULAR_REDIRECT(6),
    CONNECT_EXCEPTION(7),
    CONTENT_AP_NOT_FOUND(8),
    INVALID_URL(9),
    ATTACHMENT_AP_NOT_FOUND(10),
    MULTIMEDIA_AP_NOT_FOUND(11);

    public final int intValue;

    CheckStatus(int intValue) {
        this.intValue = intValue;
    }

    public static CheckStatus getFromInt(int intValue){
        for (CheckStatus checkStatus : CheckStatus.values()) {
            if(intValue == checkStatus.intValue){
                return checkStatus;
            }
        }
        throw new IllegalArgumentException("Unknown intValue for CheckStatus");
    }
}
