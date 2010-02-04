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

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class SpellcheckerHelperTest {

    
    @Test
    public void shouldReturnCorrectLanguageStringWithEnglishAsDefault() {
        String defaultLang = "en_us";
        List<SpellcheckerInfo> spellcheckerInfos = new ArrayList<SpellcheckerInfo>();
        spellcheckerInfos.add(new SpellcheckerInfo("en_us", "English (US)", null));
        spellcheckerInfos.add(new SpellcheckerInfo("no_nb", "Norwegian (Bokmål)", null));
        spellcheckerInfos.add(new SpellcheckerInfo("no_nn", "Norwegian (Nynorsk)", null));

        String langString = SpellcheckerHelper.getTinyMCESpellcheckerLanguages(spellcheckerInfos, defaultLang);
        assertEquals("+English (US)=en_us,Norwegian (Bokmål)=no_nb,Norwegian (Nynorsk)=no_nn", langString);
    }

    @Test
    public void shouldReturnCorrectLanguageStringWithNorwegianBokmaalAsDefault() {
        String defaultLang = "no_nb";
        List<SpellcheckerInfo> spellcheckerInfos = new ArrayList<SpellcheckerInfo>();
        spellcheckerInfos.add(new SpellcheckerInfo("en_us", "English (US)", null));
        spellcheckerInfos.add(new SpellcheckerInfo("no_nb", "Norwegian (Bokmål)", null));
        spellcheckerInfos.add(new SpellcheckerInfo("no_nn", "Norwegian (Nynorsk)", null));

        String langString = SpellcheckerHelper.getTinyMCESpellcheckerLanguages(spellcheckerInfos, defaultLang);
        assertEquals("English (US)=en_us,+Norwegian (Bokmål)=no_nb,Norwegian (Nynorsk)=no_nn", langString);
    }

}
