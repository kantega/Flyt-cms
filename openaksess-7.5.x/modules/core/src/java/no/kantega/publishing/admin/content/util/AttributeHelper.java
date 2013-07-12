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

package no.kantega.publishing.admin.content.util;

import no.kantega.commons.util.RegExp;
import no.kantega.commons.exception.RegExpSyntaxException;

public class AttributeHelper {
    private static final String REGEXP = "[^a-zA-Z0-9\\$]";


    public static String getInputFieldName(String name) {
        try {
            name = name.replace(".", "$");
            return "attributeValue_" + RegExp.replace(REGEXP, name, "_");
        } catch (RegExpSyntaxException e) {
            return "attributeValue_" + name;
        }
    }

    public static String getInputContainerName(String name) {
        try {
            name = name.replace(".", "$");
            return "contentAttribute_" + RegExp.replace(REGEXP, name, "_");
        } catch (RegExpSyntaxException e) {
            return "contentAttribute_" + name;
        }
    }

}