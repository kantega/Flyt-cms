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
import no.kantega.publishing.common.data.ListOption;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class EnumlistAttribute extends ListAttribute {
    private static final Logger log = LoggerFactory.getLogger(EnumlistAttribute.class);

	protected List<ListOption> options = null;

    @Override
	public void setConfig(Element config, Map<String, String> model)throws InvalidTemplateException, SystemException {
		super.setConfig(config, model);
		if (config != null) {
			options = new ArrayList<ListOption>();
			loadEnumValues(config);
		}
	}

	private void loadEnumValues(Element config) {		
		String enumclassName = config.getAttribute("enumclass");
		if (enumclassName != null) {
			try {
				Class enumclass = Class.forName(enumclassName);
				for (Object enumValue : enumclass.getEnumConstants()) {
					options.add(asListOption(enumValue));
				}
			} catch (ClassNotFoundException e) {
                log.error("Could not create class " + enumclassName, e);
			}
		}
	}

	private ListOption asListOption(Object enumValue) {
		ListOption option = new ListOption();
		option.setText(enumValue.toString().toLowerCase());
		option.setValue(enumValue.toString());
		return option;
	}
	
	protected List<ListOption> getOptions() {
		return options;
	}
}
