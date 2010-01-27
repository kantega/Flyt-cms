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
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

/**
 *
 */
public class SpellcheckerServiceImpl implements SpellcheckerService {

    private String dictionaryDir;


    public List<String> spellcheck(List<String> words) {
        List<String> retVal = new ArrayList<String>();
        SpellChecker c = getSpellChecker();
        if (c != null) {
            for (String word : words) {
                if (!c.isCorrect(word)) {
                    retVal.add(word);
                }
            }
        }
        return retVal;
    }

    public List<String> suggest(String word) {
        List<String> retVal = new ArrayList<String>();
        SpellChecker c = getSpellChecker();
        if (c != null) {
            retVal = c.getDictionary().getSuggestions(word, 5);
        }
        return retVal;
    }

    private SpellChecker getSpellChecker() {
        SpellChecker c = null;
        try {
//            File f = new File(getClass().getResource("en.zip").toURI());
//            File f = new File(Configuration.getApplicationDirectory() + File.separator + "dictionaries" + File.separator + "en_us.zip");
            File f = new File(dictionaryDir + File.separator + "en_us.zip");
            ZipFile zipFile = new ZipFile(f);
            SpellDictionary dictionary = new OpenOfficeSpellDictionary(zipFile);
            c = new SpellChecker(dictionary);
            c.setCaseSensitive(false);
        } catch (IOException e) {
            Log.error(getClass().getName(), e, "getSpellChecker", null);
        }
        return c;
    }

    private SpellDictionary getDictionary() {
        return null;
    }

    public void setDictionaryDir(String dictionaryDir) {
        this.dictionaryDir = dictionaryDir;
    }

}
