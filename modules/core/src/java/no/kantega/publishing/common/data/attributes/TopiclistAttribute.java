/*
 * Copyright 2009-2011 Kantega AS
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
import no.kantega.commons.util.StringHelper;
import no.kantega.publishing.admin.content.behaviours.attributes.MapAttributeValueToContentPropertyBehaviour;
import no.kantega.publishing.admin.content.behaviours.attributes.MapTopiclistAttributeValueToContentPropertyBehaviour;
import no.kantega.publishing.admin.content.behaviours.attributes.UpdateAttributeFromRequestBehaviour;
import no.kantega.publishing.admin.content.behaviours.attributes.UpdateListAttributeFromRequestBehaviour;
import no.kantega.publishing.common.data.ListOption;
import no.kantega.publishing.common.data.attributes.util.TopicAttributeValueParser;
import no.kantega.publishing.common.data.enums.AttributeProperty;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import no.kantega.publishing.topicmaps.ao.TopicAO;
import no.kantega.publishing.topicmaps.ao.TopicMapAO;
import no.kantega.publishing.topicmaps.data.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isNotBlank;

public class TopiclistAttribute extends ListAttribute {
    private static final Logger log = LoggerFactory.getLogger(TopiclistAttribute.class);
    private int topicMapId = -1;
    private String instanceOf = null;

    @Override
    public void setConfig(Element config, Map<String, String> model) throws InvalidTemplateException, SystemException {
        super.setConfig(config, model);

        if (config != null) {
            String topicmapid = config.getAttribute("topicmapid");
            if (topicmapid != null && topicmapid.length() > 0) {
                if (StringHelper.isNumeric(topicmapid)) {
                    this.topicMapId = Integer.parseInt(topicmapid, 10);
                } else {
                    this.topicMapId = TopicMapAO.getTopicMapByName(topicmapid).getId();
                }

            }
            this.instanceOf = config.getAttribute("topicinstanceof");
        }
    }

    public boolean getMultiple() {
        return multiple;
    }

    public List<ListOption> getListOptions(int language) {
        List<ListOption> options = new ArrayList<>();

        List<Topic> topics = Collections.emptyList();

        try {
            if (isNotBlank(instanceOf) && topicMapId != -1) {
                Topic instance = new Topic();
                instance.setTopicMapId(topicMapId);
                instance.setId(instanceOf);
                topics = TopicAO.getTopicsByInstance(instance);
            } else if (topicMapId != -1) {
                topics = TopicAO.getTopicsByTopicMapId(topicMapId);
            } else {
                topics = TopicAO.getAllTopics();
            }
        } catch (Exception e) {
            log.error("", e);
        }

        for (Topic topic : topics) {
            ListOption option = new ListOption();
            option.setText(topic.getBaseName());
            option.setValue(topic.getTopicMapId() + ":" + topic.getId());
            options.add(option);
        }
        return options;
    }

    public UpdateAttributeFromRequestBehaviour getUpdateFromRequestBehaviour() {
        return new UpdateListAttributeFromRequestBehaviour();
    }

    public MapAttributeValueToContentPropertyBehaviour getMapAttributeValueToContentPropertyBehaviour() {
        return new MapTopiclistAttributeValueToContentPropertyBehaviour();
    }

    public int getTopicMapId() {
        if (isNotBlank(value)) {
            return TopicAttributeValueParser.getTopicMapId(value);
        } else {
            return topicMapId;
        }
    }

    public String getInstanceOf() {
        return instanceOf;
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

    @Override
    public String getProperty(String property) {
        if (property.equalsIgnoreCase(AttributeProperty.TOPICID)) {
            return getTopicId();
        } else if (property.equalsIgnoreCase(AttributeProperty.TOPICMAPID)) {
            return String.valueOf(getTopicMapId());
        } else {
            return super.getProperty(property);
        }
    }
}
