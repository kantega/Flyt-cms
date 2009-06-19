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

import java.util.List;
import java.util.ArrayList;


/**
 * Inneholder en liste over valideringsfeil med navn på felt og feilmelding
 */

public class ValidationErrors {
    List errors = new ArrayList();

    public ValidationErrors() {
    }

    /**
     * Legg til en feilmelding
     */
    public void add(String field, String message) {
        errors.add(new ValidationError(field, message));
    }

    /**
     * Returnerer antall feil
     */
    public int getLength() {
        return errors.size();
    }

    private ValidationError item(int i) {
        return (ValidationError)errors.get(i);
    }

    /**
     * Hva slags feil som har oppstått
     */
    public String getMessage(int i) {
        return item(i).getMessage();
    }

    /**
     * Hvilket felt det er feil i
     */
    public String getField(int i) {
        return item(i).getField();
    }

    public List getErrors() {
        return errors;
    }
}
