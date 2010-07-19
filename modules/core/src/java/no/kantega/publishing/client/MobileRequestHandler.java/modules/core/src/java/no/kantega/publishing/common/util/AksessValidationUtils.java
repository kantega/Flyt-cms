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

/**
 * Author: Kristian Lier Selnæs, Kantega AS
 * Date: May 4, 2007
 * Time: 9:42:39 AM
 */
public class AksessValidationUtils {


    public static boolean isTextOnly(String str) {
        String regexp = "^[a-zA-ZæÆøØåÅöÖäÄ\\-\\s]{1,30}$";
        return str.matches(regexp);
    }


    /**
     * Checks whether a text containing special chars, such as '-' or '<', is contains only a legal set og characters.
     *
     * All letters, numbers and whitespaces are by default legal.
     *
     * @param text
     * @param legalChars Ex: {'.', ',', '-', '_', '?', '!', '@', '(', ')'}
     * @return
     */
    public static boolean isValidText(String text, char[] legalChars) {

        if(legalChars == null) {
            legalChars = new char[0];
        }

        if (text == null || "".equals(text.trim())) {
			return true;
		}
		char[] input = text.toCharArray();

		for (int i = 0; i < input.length; i++) {
			//First check if it's a letter or digit
			if (!Character.isLetterOrDigit(input[i]) && !Character.isWhitespace(input[i])) {
				//It's not a letter or digit so it better be a legal char
				if (!validateChars(input[i], legalChars)) {
					//It's not a legal char

					return false;
				}
			}
		}
		return true;

    }


    private static boolean validateChars(char c, char[] legalChars) {
		for (int i = 0; i < legalChars.length; i++) {
			if (c == legalChars[i]) {
				return true;
			}
		}
		return false;
	}
}
