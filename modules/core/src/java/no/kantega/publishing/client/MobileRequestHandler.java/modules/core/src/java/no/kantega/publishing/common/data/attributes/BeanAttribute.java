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

import org.w3c.dom.Element;

import java.util.Map;
import java.util.Iterator;
import java.util.List;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import no.kantega.publishing.common.data.ListOption;
import no.kantega.publishing.spring.RootContext;

public class BeanAttribute extends ListAttribute {

    private Class clazz;

    public List getListOptions(int language) {
        Map beans =  RootContext.getInstance().getBeansOfType(clazz);
        Iterator i = beans.keySet().iterator();
        while(i.hasNext()) {
            String id = (String)i.next();

            ListOption option = new ListOption();
            option.setValue(id);
            option.setText(id +" ("+beans.get(id).getClass().getName() +")");
            options.add(option);
        }

        return options;
    }

    public void setConfig(Element config, Map model) throws InvalidTemplateException, SystemException {
        super.setConfig(config, model);
        String clazz = config.getAttribute("class");
        if(clazz == null || clazz.trim().equals("")) {
            throw new InvalidTemplateException("Attributtet class må være satt for type=bean", "", null);
        }

        try {
            this.clazz = Class.forName(clazz);
        } catch (ClassNotFoundException e) {
            throw new InvalidTemplateException("Finner ikke klassen " + clazz, "", null);
        }
    }
}
