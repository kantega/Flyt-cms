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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Container for a list of <code>ValidationError</code>
 */
public class ValidationErrors {
    List<ValidationError> errors = new ArrayList<>();

    /**
     * Add error message
     * @param field - name of field with error
     * @param message - error message
     * @param parameters - parameters
     */
    public void add(String field, String message, Map<String, Object> parameters) {
        errors.add(new ValidationError(field, message, parameters));
    }

    /**
     * Add error message
     * @param field - name of field with error
     * @param message - error message
     */
    public void add(String field, String message) {
        errors.add(new ValidationError(field, message));
    }

    public void add(ValidationError error) {
        errors.add(error);
    }

    /**
     * @return number of validation errors
     */
    public int getLength() {
        return errors.size();
    }

    private ValidationError item(int i) {
        return errors.get(i);
    }

    /**
     * Get error message
     * @param i - index
     * @param locale - locale
     * @return - localized string
     */
    public String getMessage(int i, Locale locale) {
        return item(i).getMessage(locale);
    }

    /**
     * Get the field with error
     * @param i - index
     * @return field with index i
     */
    public String getField(int i) {
        return item(i).getField();
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    /**
     * Add errors
     * @param errors - errors
     */
    public void addAll(ValidationErrors errors) {
        this.errors.addAll(errors.getErrors());
    }

    @Override
    public String toString() {
        return "ValidationErrors{" +
                "errors=" + errors +
                '}';
    }
}
