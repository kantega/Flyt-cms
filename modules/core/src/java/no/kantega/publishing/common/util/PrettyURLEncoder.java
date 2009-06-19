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

package no.kantega.publishing.common.util;

import no.kantega.commons.util.RegExp;
import no.kantega.commons.exception.RegExpSyntaxException;
import no.kantega.commons.log.Log;

import java.util.Date;

/**
 * User: Anders Skar, Kantega AS
 * Date: Feb 29, 2008
 * Time: 3:09:38 PM
 */
public class PrettyURLEncoder {
    private static final String SOURCE = "aksess.PrettyURLEncoder";

    private static char[] space = {' ', '-'};
    private static char[] aa = {'\u00E5', 'a'};     // å
    private static char[] AA = {'\u00C5', 'A'};     // Å
    private static char[] auml = {'\u00E4', 'a'};   // ä
    private static char[] Auml = {'\u00C4', 'A'};   // Ä
    private static char[] aelig = {'\u00E6', 'a'};  // æ
    private static char[] Aelig = {'\u00C6', 'A'};  // Æ

    private static char[] oslash = {'\u00F8', 'o'}; // ø
    private static char[] Oslash = {'\u00D8', 'O'}; // Ø
    private static char[] ouml = {'\u00F6', 'o'};   // ö
    private static char[] Ouml = {'\u00D6', 'O'};   // Ö

    private static char[][] map = { space, aa, AA, auml, Auml, aelig, Aelig, oslash, Oslash, ouml, Ouml};

    public static String encode(String url) {
        for (int i = 0; i < map.length; i++) {
            if (url.indexOf(map[i][0]) != -1) {
                url = url.replace(map[i][0], map[i][1]);
            }
        }
        try {
            url = RegExp.replace("^a-zA-Z_0-9-+()", url, "");
        } catch (RegExpSyntaxException e) {
            Log.error(SOURCE, e, null, null);
        }

        return url;
    }
}
