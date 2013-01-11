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
import no.kantega.commons.log.Log;
import no.kantega.publishing.admin.content.behaviours.attributes.UpdateAttributeFromRequestBehaviour;
import no.kantega.publishing.admin.content.behaviours.attributes.UpdateListAttributeFromRequestBehaviour;
import no.kantega.publishing.api.content.Language;
import no.kantega.publishing.common.data.ListOption;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.transform.TransformerException;
import java.util.*;

/**
 *
 */
public class ListAttribute extends Attribute {
    protected boolean multiple = false;
    protected List<ListOption> options = null;

    @Override
    public void setConfig(Element config, Map<String, String> model) throws InvalidTemplateException, SystemException {
        super.setConfig(config, model);

        if (config != null) {
            String multiple = config.getAttribute("multiple");
            if ("true".equalsIgnoreCase(multiple)) {
                this.multiple = true;
            }

            options = new ArrayList<ListOption>();

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
                Log.error(getClass().getName(), e);
            }
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
        if (getOptions() == null) {
            return Collections.emptyList();
        }
        return getOptions();
    }

    public UpdateAttributeFromRequestBehaviour getUpdateFromRequestBehaviour() {
        return new UpdateListAttributeFromRequestBehaviour();
    }
    
    protected List<ListOption> getOptions(){
    	return options;
    }

    public List<String> getValues() {
        List<String> values = new ArrayList<String>();
        if (value != null && value.length() > 0) {
            String[] tmp = value.split(",");
            values.addAll(Arrays.asList(tmp));
        }

        return values;
    }    
}
