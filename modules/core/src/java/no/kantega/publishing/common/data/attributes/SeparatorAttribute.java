/*
 * Copyright 2015 Kantega AS
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

import no.kantega.publishing.admin.content.behaviours.attributes.PersistAttributeBehaviour;
import no.kantega.publishing.admin.content.behaviours.attributes.SkipPersistAttributeBehaviour;

import java.util.List;

/*
* A RepeaterAttribute is a composite Attribute, used to make repeatable rows with attributes
*/
public class SeparatorAttribute extends Attribute {



    public SeparatorAttribute() {
        super();
    }


    private void setParent(List<Attribute> attributes) {
        for (Attribute attribute : attributes) {
            attribute.setParent(this);
        }
    }

    public String getRenderer() {
        return "separator";
    }

    @Override
    public PersistAttributeBehaviour getSaveBehaviour() {
        return new SkipPersistAttributeBehaviour();
    }
}
