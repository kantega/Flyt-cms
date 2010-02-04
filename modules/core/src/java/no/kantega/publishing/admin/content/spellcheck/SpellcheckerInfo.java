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

import org.dts.spell.SpellChecker;

/**
 *
 */
public class SpellcheckerInfo {

    private String dictionary;
    private String name;
    private SpellChecker spellChecker;


    public SpellcheckerInfo(String dictionary, String name, SpellChecker spellChecker) {
        this.dictionary = dictionary;
        this.name = name;
        this.spellChecker = spellChecker;
    }

    public String getDictionary() {
        return dictionary;
    }

    public String getName() {
        return name;
    }

    public SpellChecker getSpellChecker() {
        return spellChecker;
    }

}
