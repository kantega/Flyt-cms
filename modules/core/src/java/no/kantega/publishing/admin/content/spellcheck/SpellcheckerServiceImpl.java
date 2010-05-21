/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.admin.content.spellcheck;

import no.kantega.commons.log.Log;
import org.dts.spell.SpellChecker;
import org.dts.spell.dictionary.OpenOfficeSpellDictionary;
import org.dts.spell.dictionary.SpellDictionary;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.net.URL;

/**
 *
 */
public class SpellcheckerServiceImpl implements SpellcheckerService {

    private static final String PROPERTIES_RESOURCE_NAME = "META-INF/openaksess/dictionaries/dictionary.properties";
    private static final String DICTIONARY_RESOURCE_NAME_PREFIX = "META-INF/openaksess/dictionaries/";
    private static final String DICTIONARY_RESOURCE_NAME_SUFFIX = ".zip";
    
    private Map<String, SpellChecker> spellCheckers;


    /**
     * {@inheritDoc}
     */
    public List<String> spellcheck(List<String> words, String lang) {
        List<String> retVal = new ArrayList<String>();
        SpellChecker c = getSpellChecker(lang);
        if (c != null) {
            retVal = doSpellcheck(words, c);
        } else {
            Log.error(getClass().getSimpleName(), "No SpellChecker found for language '" + lang + "'.", null, null);
        }
        return retVal;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> suggest(String word, String lang) {
        List<String> retVal = new ArrayList<String>();
        SpellChecker c = getSpellChecker(lang);
        if (c != null) {
            retVal = c.getDictionary().getSuggestions(word, 5);
        } else {
            Log.error(getClass().getSimpleName(), "No SpellChecker found for language '" + lang + "'.", null, null);
        }
        return retVal;
    }

    /**
     * {@inheritDoc}
     */
    public boolean supportsLocale(Locale locale) {
        return getSpellChecker(locale.toString()) != null;
    }

    private synchronized Map<String, SpellChecker> getSpellCheckers() {
        if (spellCheckers == null) {
            spellCheckers = new HashMap<String, SpellChecker>();
            loadSpellcheckers();
        }
        return spellCheckers;
    }

    private SpellChecker getSpellChecker(String locale) {
        return getSpellCheckers().get(locale);
    }

    private List<String> doSpellcheck(List<String> words, SpellChecker c) {
        List<String> retVal = new ArrayList<String>();
        for (String word : words) {
            if (!c.isCorrect(word)) {
                retVal.add(word);
            }
        }
        return retVal;
    }

    private void loadSpellcheckers() {
        try {
            Enumeration<URL> enumer = getClass().getClassLoader().getResources(PROPERTIES_RESOURCE_NAME);

            while (enumer.hasMoreElements()) {
                URL url = enumer.nextElement();
                try {
                    // Load properties
                    Properties p = new Properties();
                    p.load(url.openStream());
                    String dict = p.getProperty("dictionary");
                    if (dict != null) {
                        SpellChecker c = loadSpellchecker(dict);
                        if (c != null) {
                            spellCheckers.put(dict, c);
                        }
                    } else {
                        Log.error(getClass().getSimpleName(), "Missing property in '" + url + "'.", null, null);
                    }

                } catch (IOException e) {
                    Log.error(getClass().getName(), e, "getSpellChecker", null);
                }
            }
            StringBuilder builder = new StringBuilder();
            for (String d : spellCheckers.keySet()) {
                builder.append(d).append(", ");
            }
            String loadedDicts = builder.length() > 2 ? builder.toString().substring(0, builder.length() - 2) : builder.toString();
            Log.info(getClass().getSimpleName(), "Successfully loaded dictionaries for: " + loadedDicts, null, null);
        } catch (IOException e) {
            Log.error(getClass().getName(), e, "getSpellChecker", null);
        }
    }

    private SpellChecker loadSpellchecker(String dict) throws IOException {
        SpellChecker c = null;
        dict = dict.toLowerCase();
        String resource = DICTIONARY_RESOURCE_NAME_PREFIX + dict + DICTIONARY_RESOURCE_NAME_SUFFIX;
        URL zipUrl = getClass().getClassLoader().getResource(resource);
        if (zipUrl != null) {
            SpellDictionary dictionary = new OpenOfficeSpellDictionary(zipUrl.openStream(), (File)null);
            c = new SpellChecker(dictionary);
            c.setCaseSensitive(false);
            c.setIgnoreUpperCaseWords(true);
        } else {
            Log.error(getClass().getSimpleName(), "Could not find resource '" + resource + "'.", null, null);
        }
        return c;
    }

}
