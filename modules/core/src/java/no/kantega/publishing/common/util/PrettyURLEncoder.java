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

import no.kantega.publishing.common.Aksess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class PrettyURLEncoder {
    private static final Logger log = LoggerFactory.getLogger(PrettyURLEncoder.class);

    private static char[] space = {' ', '-'};
    private static char[] slash = {'/', '-'};
    private static char[] aa = {'\u00E5', 'a'};
    private static char[] AA = {'\u00C5', 'A'};
    private static char[] auml = {'\u00E4', 'a'};
    private static char[] Auml = {'\u00C4', 'A'};
    private static char[] aelig = {'\u00E6', 'a'};
    private static char[] Aelig = {'\u00C6', 'A'};

    private static char[] oslash = {'\u00F8', 'o'};
    private static char[] Oslash = {'\u00D8', 'O'};
    private static char[] ouml = {'\u00F6', 'o'};
    private static char[] Ouml = {'\u00D6', 'O'};

    private static char[][] map = { space, slash, aa, AA, auml, Auml, aelig, Aelig, oslash, Oslash, ouml, Ouml};

    public static final String LEGAL_URL_PATTERN = "[^a-zA-Z_0-9-+\\.:]";
    private static Pattern pattern = Pattern.compile(LEGAL_URL_PATTERN);

    public static String encode(String url) {
        if (url == null) {
            return "";
        }
        String prettyUrl = url.trim();
        for (char[] aMap : map) {
            if (prettyUrl.indexOf(aMap[0]) != -1) {
                prettyUrl = prettyUrl.replace(aMap[0], aMap[1]);
            }
        }
        try {
            prettyUrl = pattern.matcher(prettyUrl).replaceAll("");
        } catch (Exception e) {
            log.error("error replacing illegal chars", e);
        }

        return prettyUrl;
    }

    public static String createContentUrl(int associationId, String title) {
        return createContentUrl(associationId, title, null);
    }    

    public static String createContentUrl(int associationId, String title, String alias) {
        if (isNotBlank(alias)) {
            return alias.charAt(0) == '/' ? alias : "/" + alias;
        } else {
            return Aksess.CONTENT_URL_PREFIX + "/" + associationId + "/" + encode(title);
        }

    }

    public static String createMultimediaUrl(int multimediaId, String filename) {
        return Aksess.MULTIMEDIA_URL_PREFIX + "/" + multimediaId + "/" + encode(filename);
    }

}
