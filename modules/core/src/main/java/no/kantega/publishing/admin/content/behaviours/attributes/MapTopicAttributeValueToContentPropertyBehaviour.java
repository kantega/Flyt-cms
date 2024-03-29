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

package no.kantega.publishing.admin.content.behaviours.attributes;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.TopicAttribute;
import no.kantega.publishing.common.data.enums.ContentProperty;


public class MapTopicAttributeValueToContentPropertyBehaviour  implements MapAttributeValueToContentPropertyBehaviour {
    public void mapAttributeValue(RequestParameters param, Content content, Attribute attribute, String field, ValidationErrors errors) {
        if (field != null) {
            if (field.equalsIgnoreCase(ContentProperty.TOPICS)) {
                TopicAttribute topicAttribute = (TopicAttribute)attribute;
                content.getTopics().addAll(topicAttribute.getValueAsTopics());
            }
        }
    }
}
