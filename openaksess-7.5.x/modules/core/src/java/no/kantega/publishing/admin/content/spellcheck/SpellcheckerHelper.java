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

import java.util.List;

/**
 *
 */
public class SpellcheckerHelper {


    public static String getTinyMCESpellcheckerLanguages(List<SpellcheckerInfo> infoList, String defaultLang) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < infoList.size(); i++) {
            SpellcheckerInfo info = infoList.get(i);
            if (info.getDictionary().equals(defaultLang)) {
                builder.append("+");
            }
            builder.append(info.getName()).append("=").append(info.getDictionary());
            if (i < infoList.size() - 1) {
                builder.append(",");
            }
        }
        return builder.toString();
    }

}
