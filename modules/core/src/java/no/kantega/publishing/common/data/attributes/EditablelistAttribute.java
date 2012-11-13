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

package no.kantega.publishing.common.data.attributes;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.EditableListAO;
import no.kantega.publishing.common.data.ListOption;
import no.kantega.publishing.common.data.enums.AttributeProperty;
import no.kantega.publishing.common.data.enums.Language;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Map;

public class EditablelistAttribute extends ListAttribute {

    private String key;
    private String[] editableBy;
    private boolean ignoreVariant;

    @Override
    public void setConfig(Element config, Map<String, String> model) throws InvalidTemplateException, SystemException {
        super.setConfig(config, model);

        if (config != null) {

            key = config.getAttribute("key");
            if(key == null || key.trim().length() == 0) {
                key = getName();
            }

            String strEditableBy = config.getAttribute("editableby"); // For backwards compability
            if (strEditableBy == null || strEditableBy.length() == 0) {
                strEditableBy = config.getAttribute("listeditablebyrole");
            }

            if (strEditableBy != null && strEditableBy.length() > 0) {
                editableBy = strEditableBy.split(",");
                for (int i = 0; i < editableBy.length; i++) {
                    editableBy[i] = editableBy[i].trim();
                }
            } else {
                editableBy = new String[]{Aksess.getEveryoneRole()};                
            }
            ignoreVariant = Boolean.valueOf(config.getAttribute("ignorevariant"));
        }

    }


    public String getProperty(String property) {
        String returnValue = value;
        if (value == null || value.length() == 0) {
            return null;
        }
        if (AttributeProperty.HTML.equalsIgnoreCase(property)) {
            //Add a space after each element in the list
            returnValue = value.replaceAll(",", ", ");
        }
        return returnValue;
    }

    public String getRenderer() {
        return "editablelist";
    }


    public String getKey() {
        return key;
    }

    public String[] getEditableBy() {
        return editableBy;
    }

    public List<ListOption> getListOptions(int language) {
        return EditableListAO.getOptions(key, Language.getLanguageAsLocale(language), ignoreVariant);
    }
}
