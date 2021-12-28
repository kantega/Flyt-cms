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
import no.kantega.publishing.spring.RootContext;
import org.w3c.dom.Element;

import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class BeanAttribute extends ListAttribute {

    private Class clazz;

    public List<ListOption> getListOptions(int language) {

        Map<String, ?> beans =  RootContext.getInstance().getBeansOfType(clazz);
        for (Map.Entry<String, ?> bean : beans.entrySet()) {
            String beanClass = bean.getValue().getClass().getName();
            String id = bean.getKey();
            ListOption option = new ListOption();
            option.setValue(id);
            option.setText(id + " (" + beanClass + ")");
            options.add(option);
        }

        return options;
    }

    @Override
    public void setConfig(Element config, Map<String, String> model) throws InvalidTemplateException, SystemException {
        super.setConfig(config, model);
        String clazz = config.getAttribute("class");
        if(isBlank(clazz)) {
            throw new InvalidTemplateException("Attributtet class må være satt for type=bean",null);
        }

        try {
            this.clazz = Class.forName(clazz);
        } catch (ClassNotFoundException e) {
            throw new InvalidTemplateException("Finner ikke klassen " + clazz, null);
        }
    }
}
