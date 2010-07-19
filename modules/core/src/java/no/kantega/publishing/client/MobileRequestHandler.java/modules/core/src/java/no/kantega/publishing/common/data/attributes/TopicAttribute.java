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
        List<Topic> topics = getValueAsTopics();
        if (topics.size() == 0) {
            return null;
        } else {
            return topics.get(0);
        }
    }

    public List<Topic> getValueAsTopics() {
        List<Topic> topicList = new ArrayList<Topic>();

        if (value == null || value.indexOf("") == -1) {
            return topicList;
        }

        String[] topics = getValue().split(",");
        for (int i = 0; i < topics.length; i++) {
            String[] topicStrings = topics[i].split(":");
            if(topicStrings.length == 2) {
                int topicMapId = Integer.parseInt(topicStrings[0]);
                String topicId = topicStrings[1];
                Topic topic = TopicAO.getTopic(topicMapId, topicId);
                if (topic != null) {
                    topicList.add(topic);
                }
            }
        }
        return topicList;
    }

    public String getTopicId() {
        Topic topic = getValueAsTopic();
        if (topic == null) {
            return null;
        } else {
            return topic.getId();
        }
    }

    public int getTopicMapId() {
        Topic topic = getValueAsTopic();
        if (topic == null) {
            return -1;
        } else {
            return topic.getTopicMapId();
        }
    }

    public MapAttributeValueToContentPropertyBehaviour getMapAttributeValueToContentPropertyBehaviour() {
        return new MapTopicAttributeValueToContentPropertyBehaviour();
    }    
}

