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
import no.kantega.publishing.common.exception.InvalidTemplateException;
import org.w3c.dom.Element;

import java.util.Map;

public class OrgunitAttribute extends Attribute {

    private boolean multiple = false;

    private boolean moveable = true;

    @Override
    public void setConfig(Element config, Map<String, String> model) throws InvalidTemplateException, SystemException {
        super.setConfig(config, model);

        if (config != null) {
            multiple = "true".equals(config.getAttribute("multiple"));
        }
    }
    public String getRenderer() {
        return multiple ? "orgunit_multiple" : "orgunit";
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    public boolean isMoveable() {
        return moveable;
    }

    public void setMoveable(boolean moveable) {
        this.moveable = moveable;
    }
}
