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

import no.kantega.publishing.common.data.attributes.util.TopicAttributeValueParser;
import no.kantega.publishing.common.data.enums.AttributeProperty;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.topicmaps.ao.TopicAO;
import no.kantega.publishing.admin.content.behaviours.attributes.MapAttributeValueToContentPropertyBehaviour;
import no.kantega.publishing.admin.content.behaviours.attributes.MapTopiclistAttributeValueToContentPropertyBehaviour;
import no.kantega.publishing.admin.content.behaviours.attributes.MapTopicAttributeValueToContentPropertyBehaviour;
import no.kantega.commons.exception.SystemException;

import java.text.ParseException;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.w3c.dom.Element;

public class TopicAttribute  extends Attribute {
    protected boolean multiple = false;

    public void setConfig(Element config, Map model) throws InvalidTemplateException, SystemException {
        super.setConfig(config, model);

        if (config != null) {
            String multiple = config.getAttribute("multiple");
            if ("true".equalsIgnoreCase(multiple)) {
                this.multiple = true;
            }
        }
    }

    public String getRenderer() {
        if (multiple) {
            return "topic_multiple";
        } else {
            return "topic";
        }
    }

    public Topic getValueAsTopic() {
        return TopicAttributeValueParser.getValueAsTopic(value);
    }

    public List<Topic> getValueAsTopics() {
        return TopicAttributeValueParser.getValueAsTopics(value);
    }

    public String getTopicId() {
        return TopicAttributeValueParser.getTopicId(value);
    }

    public int getTopicMapId() {
        return TopicAttributeValueParser.getTopicMapId(value);
    }

    public MapAttributeValueToContentPropertyBehaviour getMapAttributeValueToContentPropertyBehaviour() {
        return new MapTopicAttributeValueToContentPropertyBehaviour();
    }

    @Override
    public String getProperty(String property) {
        if (property.equalsIgnoreCase(AttributeProperty.TOPICID)) {
            return getTopicId();
        } else if (property.equalsIgnoreCase(AttributeProperty.TOPICMAPID)) {
            return "" + getTopicMapId();
        } else {
            return super.getProperty(property);
        }
    }
}

