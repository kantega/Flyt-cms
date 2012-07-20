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

package no.kantega.search.criteria;

import no.kantega.search.index.Fields;

/**
 * Criterion som søker etter ett eller flere ord i et felt.
 *
 * Date: Dec 5, 2008
 * Time: 11:06:35 AM
 *
 * @author Tarje Killingberg
 */
public class TextCriterion extends FieldCriterion{

    public final static String DEFAULT_FIELDNAME = Fields.CONTENT;
    private static final String SOURCE = TextCriterion.class.getName();

    private String fieldname;
    private String text;


    public TextCriterion(String text) {
        this(DEFAULT_FIELDNAME, text);
    }

    public TextCriterion(String fieldname, String text) {
        super(fieldname, text);
    }
}
