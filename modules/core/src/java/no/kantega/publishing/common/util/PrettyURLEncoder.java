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
import no.kantega.publishing.common.Aksess;

import java.util.Date;

public class PrettyURLEncoder {
    private static final String SOURCE = "aksess.PrettyURLEncoder";

    private static char[] space = {' ', '-'};
    private static char[] aa = {'\u00E5', 'a'};     // �
    private static char[] AA = {'\u00C5', 'A'};     // �
    private static char[] auml = {'\u00E4', 'a'};   // �
    private static char[] Auml = {'\u00C4', 'A'};   // �
    private static char[] aelig = {'\u00E6', 'a'};  // �
    private static char[] Aelig = {'\u00C6', 'A'};  // �

    private static char[] oslash = {'\u00F8', 'o'}; // �
    private static char[] Oslash = {'\u00D8', 'O'}; // �
    private static char[] ouml = {'\u00F6', 'o'};   // �
    private static char[] Ouml = {'\u00D6', 'O'};   // �

    private static char[][] map = { space, aa, AA, auml, Auml, aelig, Aelig, oslash, Oslash, ouml, Ouml};

    public static String encode(String url) {
        for (int i = 0; i < map.length; i++) {
            if (url.indexOf(map[i][0]) != -1) {
                url = url.replace(map[i][0], map[i][1]);
            }
        }
        try {
            url = RegExp.replace("^a-zA-Z_0-9-+().:<", url, "");
        } catch (RegExpSyntaxException e) {
            Log.error(SOURCE, e, null, null);
        }

        return url;
    }

    public static String createContentUrl(int associationId, String title) {
        return createContentUrl(associationId, title, null);
    }    

    public static String createContentUrl(int associationId, String title, String alias) {
        if (alias != null && alias.length() > 0) {
            return "/" + Aksess.CONTENT_REQUEST_HANDLER + "?thisId=" + associationId;
        } else {
            return Aksess.CONTENT_URL_PREFIX + "/" + associationId + "/" + encode(title);
        }

    }
}
