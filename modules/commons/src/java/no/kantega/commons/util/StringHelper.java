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

import java.util.StringTokenizer;

public class StringHelper {
    /**
     *
     * @param source - text to replace text in
     * @param oldPattern - string to replace
     * @param newPattern - string to replace with
     * @return - new text
     */
    public static String replace(String source, String oldPattern, String newPattern){
        StringBuffer result = new StringBuffer();
        int startIdx = 0;
        int idxOld = 0;
        while ((idxOld = source.indexOf(oldPattern, startIdx)) >= 0) {
            result.append(source.substring(startIdx, idxOld));
            result.append(newPattern);
            startIdx = idxOld + oldPattern.length();
        }
        result.append(source.substring(startIdx));
        return result.toString();
    }

    /**
     * Creates HTML links from URLs and email adresses found in text
     * @param source text to convert
     * @return - text with links
     */
    public static String makeLinks(String source) {
        StringBuffer buffer = new StringBuffer(source);
        buffer = makeLinks(buffer,"://");
        buffer = makeLinks(buffer,"www.");
        buffer = makeLinks(buffer,"@",true);
        return buffer.toString();
    }

    private static StringBuffer makeLinks(StringBuffer text, String toFind, boolean isMail){
        int urlPos  = text.indexOf(toFind);
        int foundPos  = urlPos;
        while (foundPos != -1) {
            int textLen = text.length();
            int startPos = foundPos;

            // Sjekker om lenken skal skippes
            boolean skip = false;
            while ((startPos > -1) && (!skip) ) {
                startPos--;
                if (startPos == -1) {
                    break;
                }
                char ch = text.charAt(startPos);
                if ((ch == '<') || (ch == '>') || (ch == '"')) {
                    // Sannsynligvis HTML, skip
                    skip = true;
                    break;
                }
                if ((ch == '\n') || (ch == '\r') || (ch == ' ') || (ch == '\t') || (ch == '(')) {
                    break;
                }
            }
            int endPos = foundPos;
            while ((endPos < textLen) && (!skip)) {
                endPos++;
                if (endPos == textLen) {
                    break;
                }
                char ch = text.charAt(endPos);
                if ((ch == '<') || (ch == '>') || (ch == '"')) {
                    // This is probably HTML, skip
                    skip = true;
                    break;
                }
                if ((ch == '\n') || (ch == '\r') || (ch == ' ') || (ch == '\t') || (ch == ')')) {
                    break;
                }
            }

            // Setter på link markup om funnet tekst ikke skal skippes
            if (!skip) {
                if (text.charAt(endPos - 1) == '.' || text.charAt(endPos -1) == ',')
                    endPos--;

                String address  = text.substring(startPos + 1, endPos);
                String generatedLink;
                if( isMail ) {
                    generatedLink = "<a href=\"mailto:" + address + "\">" + address + "</a>";
                }
                else {
                    if( address.indexOf("://") == -1){
                        generatedLink = "<a href=\"http://" + address + "\">" + address + "</a>";
                    }
                    else {
                        generatedLink = "<a href=\"" + address + "\">" + address + "</a>";
                    }
                }
                text.replace(startPos+1,endPos,generatedLink);
                foundPos = startPos + generatedLink.length();
            }
            foundPos  = text.indexOf(toFind, foundPos + 1);
        }
        return text;

    }

    private static StringBuffer makeLinks(StringBuffer text, String toFind){
        return makeLinks(text, toFind, false);
    }



    /**
     * Removes characters which should not be used when displaying title in Javascript
     * @param source - source text
     * @return - new text
     */
    public static String removeIllegalCharsInTitle(String source) {
        source = replace(source, "\r\n", "");
        source = replace(source, "\n", "");
        source = replace(source, "\"" ,"");
        source = replace(source, "'" ,"");

        return source;
    }


    public static String escapeQuotes(String source) {
        return source.replaceAll("\"", "&quot;");        
    }


    /**
     * Return string with ints as array of ints
     * @param source - source string
     * @param seperator - seperator used for ints
     * @return - array of ints
     */
    public static int[] getInts(String source, String seperator) {
        StringTokenizer tokens = new StringTokenizer(source, seperator);
        int ints[] = new int[tokens.countTokens()];
        int i = 0;
        while (tokens.hasMoreTokens()) {
            String tmp = tokens.nextToken();
            ints[i++] = Integer.parseInt(tmp);
        }
        return ints;
    }

    /**
     * Return string with first letter in uppercase
     * @param source - source text
     * @return - replaced text
     */
    public static String capitalize(String source) {
        String newstr = "";

        char ch = '.';
        for (int i = 0; i < source.length(); i++) {
            ch = source.charAt(i);
            if(i == 0 && Character.isLetter(ch) && Character.isLowerCase(ch)) {
                newstr += Character.toUpperCase(ch);
            } else {
                newstr += ch;
            }
        }

        return newstr;
    }

    /**
     * Strips off all html-tags
     * @param source - The text to strip
     * @return The text with tags replaced by an empty string.
     */
    public static String stripHtml(String source) {
        return source.replaceAll("<(.|\\n)+?>", "");
    }
}
