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
import no.kantega.commons.util.LocaleLabels;
import no.kantega.publishing.admin.content.behaviours.attributes.UpdateAttributeFromRequestBehaviour;
import no.kantega.publishing.admin.content.behaviours.attributes.UpdateListAttributeFromRequestBehaviour;
import no.kantega.publishing.api.content.Language;
import no.kantega.publishing.common.ao.EditableListAO;
import no.kantega.publishing.common.data.ListOption;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import org.apache.xpath.XPathAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.transform.TransformerException;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 *
 */
public class ListAttribute extends Attribute {
    private static final Logger log = LoggerFactory.getLogger(ListAttribute.class);
    protected boolean multiple = false;
    protected List<ListOption> options = null;
    protected String key;
    protected boolean ignoreVariant;

    @Override
    public void setConfig(Element config, Map<String, String> model) throws InvalidTemplateException, SystemException {
        super.setConfig(config, model);

        if (config != null) {
            String multiple = config.getAttribute("multiple");
            if ("true".equalsIgnoreCase(multiple)) {
                this.multiple = true;
            }

            key = config.getAttribute("key");
            if (key == null || key.trim().length() == 0) {
                key = "";
            }

            options = new ArrayList<>();

            try {
                NodeList nodes = XPathAPI.selectNodeList(config, "options/option");
                for (int i = 0; i < nodes.getLength(); i++) {
                    Element elmOption  = (Element)nodes.item(i);
                    String optText = elmOption.getFirstChild().getNodeValue();
                    String optVal  = elmOption.getAttribute("value");
                    String optSel  = elmOption.getAttribute("selected");
                    ListOption option = new ListOption();
                    option.setText(optText);
                    option.setValue(optVal);
                    if ("true".equalsIgnoreCase(optSel)) {
                        option.setDefaultSelected(true);
                    }
                    options.add(option);
                }

            } catch (TransformerException e) {
                log.error("Error getting list options", e);
            }
            ignoreVariant = Boolean.valueOf(config.getAttribute("ignorevariant"));
        }
    }

    public String getRenderer() {
        return "list";
    }

    public boolean getMultiple() {
        return multiple;
    }

    public List<ListOption> getListOptions() {
        return getListOptions(Language.NORWEGIAN_BO);
    }

    public List<ListOption> getListOptions(int language) {
        if (!getOptions().isEmpty()) {
            return getOptions();
        } else if (!getKey().isEmpty()) {
            List<ListOption> listOptions = EditableListAO.getOptions(key, Language.getLanguageAsLocale(language), ignoreVariant);
            if (!isSomeSelected(listOptions)) {
                Locale locale = Language.getLanguageAsLocale(language);
                ListOption listOption = new ListOption();
                listOption.setText(LocaleLabels.getLabel("aksess.list.ingen", locale));
                listOptions.add(0, listOption);
            }
            return listOptions;
        } else {
            return Collections.emptyList();
        }
    }

    public UpdateAttributeFromRequestBehaviour getUpdateFromRequestBehaviour() {
        return new UpdateListAttributeFromRequestBehaviour();
    }
    
    protected List<ListOption> getOptions(){
    	return options;
    }

    public List<String> getValues() {
        List<String> values = new ArrayList<>();
        if (isNotBlank(value)) {
            String[] tmp = value.split(",");
            values.addAll(Arrays.asList(tmp));
        }

        return values;
    }

    public String getKey() {
        return key;
    }

    private boolean isSomeSelected(List<ListOption> listOptions) {
        String value = this.getValue();
        boolean someSelected = false;
        for (ListOption option : listOptions) {
            if (isSelected(option, value)) {
                someSelected = true;
            }
        }
        return someSelected;
    }

    private boolean isSelected(ListOption option, String value) {
        String optText = option.getText();
        String optVal  = option.getValue();
        if (isBlank(optVal)) {
            optVal = optText;
        }

        boolean selected = false;
        if ((isBlank(value)) && (option.isDefaultSelected())) {
            selected = true;
        } else {
            if (value != null) {
                String[] values = value.split(",");
                for (String v : values) {
                    if (v.equalsIgnoreCase(optVal)) {
                        selected = true;
                        break;
                    }
                }
            }
        }

        return selected;
    }
}
