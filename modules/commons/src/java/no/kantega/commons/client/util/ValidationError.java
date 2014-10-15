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

package no.kantega.commons.client.util;

import no.kantega.commons.util.LocaleLabels;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

public class ValidationError {
    private final String field;
    private final String message;
    private final Map<String, Object> parameters;

    public ValidationError(String field, String message) {
        this.field = field;
        this.message = message;
        parameters = Collections.emptyMap();
    }

    public ValidationError(String field, String message, Map<String, Object> parameters) {
        this.field = field;
        this.message = message;
        this.parameters = parameters;
    }

    public String getMessage(Locale locale) {
        return LocaleLabels.getLabel(message, locale, parameters);
    }

    public String getField() {
        return field;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public String getMessage() {
        return message;
    }
}
