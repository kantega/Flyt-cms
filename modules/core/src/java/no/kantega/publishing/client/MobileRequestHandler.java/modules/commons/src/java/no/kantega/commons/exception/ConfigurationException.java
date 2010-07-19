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

package no.kantega.commons.exception;

import no.kantega.commons.log.Log;


/**
 *
 */
public class ConfigurationException extends KantegaException {
    public ConfigurationException(String message, String source) {
        super(message, source, null);
        Log.error(source, message, null, null);
    }

    public ConfigurationException(String message, Throwable original) {
        super(message, "no.kantega.secure.configuration", original);
        Log.error("no.kantega.secure.configuration", original, null, null);
    }

    public ConfigurationException(String message) {
        super(message, "no.kantega.secure.configuration", null);
        Log.error("no.kantega.secure.configuration", message, null, null);
    }
}
