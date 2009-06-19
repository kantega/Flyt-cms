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

package no.kantega.publishing.common.data.enums;

import java.util.Locale;

/**
 *
 */
public class Language {
    public static final int NORWEGIAN_BO = 0;
    public static final int NORWEGIAN_NN = 1;
    public static final int NORWEGIAN_SAMI = 2;
    public static final int ENGLISH = 3;
    public static final int GERMAN = 4;
    public static final int SPANISH = 5;
    public static final int DUTCH = 6;
    public static final int FRENCH = 7;
    public static final int SWEDISH = 8;
    public static final int DANISH = 9;
    public static final int FINNISH = 10;

    /**
     * Returnerer språkkoden i henhold til ISO639-1.
     *
     * @param language
     * @return
     */
    public static String getLanguageAsISOCode(int language) {
        switch (language) {
            case NORWEGIAN_NN:
                return "nno";
            case NORWEGIAN_SAMI:
                return "smi";
            case ENGLISH:
                return "eng";
            case GERMAN:
                return "ger";
            case SPANISH:
                return "spa";
            case DUTCH:
                return "dut";
            case FRENCH:
                return "fre"; //språkkoden fra finnes også for fransk
            case SWEDISH:
                return "swe";
            case DANISH:
                return "dan";
            case FINNISH:
                return "fin";
            default:
                return "nbo";
        }
    }

    /**
     * Returnerer språkkoden i henhold til ISO639-2.
     * Hvis det skal angies språk i html-attributtet lang anbefales det at denne standarden benyttes.
     *
     * @param language
     * @return
     */
    public static String getLanguageAsISO639_2Code(int language) {
        switch (language) {
            case NORWEGIAN_BO:
                return "nb";
            case NORWEGIAN_NN:
                return "nn";
            case NORWEGIAN_SAMI:
                return "smi";
            case ENGLISH:
                return "en";
            case GERMAN:
                return "de";
            case SPANISH:
                return "es";
            case DUTCH:
                return "nl";
            case FRENCH:
                return "fr";
            case SWEDISH:
                return "sv";
            case DANISH:
                return "da";
            case FINNISH:
                return "fi";
            default:
                return "no";
        }
    }



    public static Locale getLanguageAsLocale(int language) {
        switch (language) {
            case NORWEGIAN_NN:
                return new Locale("no", "NO", "NY");
            case NORWEGIAN_SAMI:
                return new Locale("no", "NO");
            case ENGLISH:
                return new Locale("en", "US");
            case GERMAN:
                return new Locale("de", "DE");
            case SPANISH:
                return new Locale("es", "ES");
            case DUTCH:
                return new Locale("nl", "NL");
            case SWEDISH:
                return new Locale("sv", "SE");
            case DANISH:
                 return new Locale("da", "DK");
            case FINNISH:
                 return new Locale("fi", "FI");
             default:
                return new Locale("no", "NO");
        }
    }

    public static int[] getLanguages() {
        return new int[] {NORWEGIAN_BO, NORWEGIAN_NN, NORWEGIAN_SAMI, ENGLISH, GERMAN, SPANISH, DUTCH, FRENCH, SWEDISH, DANISH};
    }
}

