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

package no.kantega.search.result;

/**
 * Date: Jan 28, 2009
 * Time: 10:14:45 AM
 *
 * @author Tarje Killingberg
 */
public class TermTranslatorDefaultImpl implements TermTranslator {

    private static final String SOURCE = TermTranslatorDefaultImpl.class.getName();


    public String fromField(String field) {
        return field;
    }

    public String fromTerm(String field, String term) {
        return term;
    }

}
