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

package no.kantega.commons.util;

import no.kantega.commons.exception.RegExpSyntaxException;

import java.util.regex.Pattern;

/**
 *
 */
public class RegExp {
    private static String EMAIL =  "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+([A-Za-z0-9-.]*)+\\.[A-Za-z0-9-]{2,4}$";

    public static boolean matches(String regexp, String input) throws RegExpSyntaxException {
        Pattern p = Pattern.compile(regexp);
        return p.matcher(input).matches();
    }

    public static String replace(String regexp, String input, String replacechar) throws RegExpSyntaxException {
        Pattern p = Pattern.compile(regexp);
        return p.matcher(input).replaceAll(replacechar);
    }

    public static boolean isEmail(String email) {
        Pattern p = Pattern.compile(EMAIL);
        return p.matcher(email).matches();
    }
}
