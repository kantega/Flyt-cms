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

import java.util.Random;

public class PasswordGenerator {

    private static final int MAX_WORDLENGTH = 8;

    private static final int COMPOSITE = 0, VOWEL = 1, CONSONANT = 2;

    public static final int CASE_SCHEME_LEADING_UPPER = 0;
    public static final int CASE_SCHEME_ALL_UPPER = 1;
    public static final int CASE_SCHEME_ALL_LOWER = 2;
    public static final int CASE_SCHEME_ONE_UPPER = 3;
    public static final int CASE_SCHEME_RANDOM = 4;

    public static final int DIGIT_SCHEME_LEADING = 0;
    public static final int DIGIT_SCHEME_TRAILING = 1;
    public static final int DIGIT_SCHEME_RANDOM = 2;

    private static String[] composite = new String[]{
        "bj", "bl", "br", "cl", "cr", "dr", "dv", "fj", "fl", "fr",
        "gj", "gl", "gr", "hj", "hv", "kj", "kl", "kn", "kr", "kv",
        "mj", "nj", "pj", "pl", "pr", "sj", "sk", "sl", "sm", "sn",
        "sp", "st", "sv", "tj", "tr", "tv", "vr"
    };

    private static String[] vowel = new String[]{
        "a", "e", "i", "o", "u", "y"
    };

    private static String[] consonant = new String[]{
        "b", "c", "d", "f", "g", "j", "k", "l", "m", "n",
        "p", "q", "r", "s", "t", "v", "w", "x", "z"
    };

    private static String[][] characters = new String[][]{
        composite, vowel, consonant
    };

    private static String[] digit = new String[]{
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
    };

    private static int[][][] schemes = new int[][][]{
        {// 0
            {}
        }, {// 1
            {CONSONANT},
            {VOWEL}
        }, {// 2
            {CONSONANT, VOWEL},
            {VOWEL, CONSONANT},
            {COMPOSITE}
        }, {// 3
            {CONSONANT, VOWEL, CONSONANT},
            {COMPOSITE, VOWEL}
        }, {// 4
            {CONSONANT, VOWEL, CONSONANT, VOWEL},
            {CONSONANT, VOWEL, VOWEL, CONSONANT},
            {COMPOSITE, VOWEL, CONSONANT}
        }, {// 5
            {CONSONANT, VOWEL, VOWEL, CONSONANT, VOWEL},
            {COMPOSITE, VOWEL, CONSONANT, CONSONANT},
            {COMPOSITE, VOWEL, CONSONANT, VOWEL}
        }, {// 6
            {CONSONANT, VOWEL, CONSONANT, CONSONANT, VOWEL, CONSONANT},
            {COMPOSITE, VOWEL, CONSONANT, CONSONANT, VOWEL},
            {COMPOSITE, VOWEL, CONSONANT, VOWEL, CONSONANT}
        }, {// 7
            {COMPOSITE, VOWEL, CONSONANT, CONSONANT, VOWEL, CONSONANT},
            {CONSONANT, VOWEL, COMPOSITE, VOWEL, CONSONANT, CONSONANT}
        }, {// 8
            {COMPOSITE, VOWEL, CONSONANT, CONSONANT, VOWEL, CONSONANT, VOWEL},
            {CONSONANT, VOWEL, COMPOSITE, VOWEL, CONSONANT, CONSONANT, VOWEL}
        }
    };

    private Random random;
    private int minNumDigits, maxNumDigits, minWordLength, maxWordLength;
    private int digitScheme, caseScheme;

    public PasswordGenerator(int digitScheme, int caseScheme, int minNumDigits, int maxNumDigits, int minWordLength, int maxWordLength) {
        if (maxWordLength > MAX_WORDLENGTH) {
            throw new IllegalStateException("Cannot generate words longer than " + MAX_WORDLENGTH + " characters");
        }
        random = new Random();
        this.digitScheme = digitScheme;
        this.caseScheme = caseScheme;
        this.minNumDigits = minNumDigits;
        this.maxNumDigits = maxNumDigits;
        this.minWordLength = minWordLength;
        this.maxWordLength = maxWordLength;
    }

    public String generate() {
        int digitLength = minNumDigits + getRandom(maxNumDigits - minNumDigits + 1);
        int leadingLength = 0;
        int trailingLength = 0;

        switch (digitScheme) {
            case DIGIT_SCHEME_LEADING:
                leadingLength = digitLength;
                break;

            case DIGIT_SCHEME_TRAILING:
                trailingLength = digitLength;
                break;

            case DIGIT_SCHEME_RANDOM:
                leadingLength = getRandom(digitLength);
                trailingLength = digitLength - leadingLength;
                break;

            default:
                throw new IllegalStateException("Illegal digit scheme");
        }


        StringBuffer password = new StringBuffer();
        password.append(generateDigits(leadingLength));
        password.append(generateWord());
        password.append(generateDigits(trailingLength));
        return password.toString();
    }

    private int[] chooseScheme(int[][] schemes) {
        int choice = getRandom(schemes.length);
        return schemes[choice];
    }

    private int getRandom(int n) {
        if (n == 0) {
            return 0;
        } else {
            return random.nextInt(n);
        }
    }

    private String generateWord() {
        int wordLength = minWordLength + getRandom(maxWordLength - minWordLength + 1);
        StringBuffer buffer = new StringBuffer(wordLength);
        int[] scheme = chooseScheme(schemes[wordLength]);
        for (int i = 0; i < scheme.length; i++) {
            if (i > 0 && scheme[i] == scheme[i - 1]) {  // Doble vokaler eller konsonanter skal være lik
                buffer.append(buffer.charAt(buffer.length() - 1));
            } else {
                String[] chars = characters[scheme[i]];
                buffer.append(chars[getRandom(chars.length)]);
            }
        }
        buffer = convertToUpper(buffer);
        return buffer.toString();
    }

    private String generateDigits(int length) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            buffer.append(digit[getRandom(digit.length)]);
        }
        return buffer.toString();
    }

    private StringBuffer convertToUpper(StringBuffer buf) {
        switch (caseScheme) {
            case CASE_SCHEME_ALL_LOWER:
                return buf;

            case CASE_SCHEME_ALL_UPPER:
                for (int i = 0; i < buf.length(); i++) {
                    buf.setCharAt(i, Character.toUpperCase(buf.charAt(i)));
                }
                return buf;

            case CASE_SCHEME_ONE_UPPER:
                int pos = getRandom(buf.length());
                buf.setCharAt(pos, Character.toUpperCase(buf.charAt(pos)));
                return buf;

            case CASE_SCHEME_LEADING_UPPER:
                buf.setCharAt(0, Character.toUpperCase(buf.charAt(0)));
                return buf;

            case CASE_SCHEME_RANDOM:
                for (int i = 0; i < buf.length(); i++) {
                    if (getRandom(2) > 0) {
                        buf.setCharAt(i, Character.toUpperCase(buf.charAt(i)));
                    }
                }
                return buf;

            default:
                return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(new PasswordGenerator(DIGIT_SCHEME_LEADING, CASE_SCHEME_ALL_LOWER, 0, 0, 8, 8).generate());
    }
}