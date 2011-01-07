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
import no.kantega.publishing.admin.content.behaviours.attributes.MapAttributeValueToContentPropertyBehaviour;
import no.kantega.publishing.admin.content.behaviours.attributes.MapTopiclistAttributeValueToContentPropertyBehaviour;
import no.kantega.publishing.admin.content.behaviours.attributes.UpdateAttributeFromRequestBehaviour;
import no.kantega.publishing.admin.content.behaviours.attributes.UpdateListAttributeFromRequestBehaviour;
import no.kantega.publishing.common.data.ListOption;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import no.kantega.search.index.Fields;
import no.kantega.publishing.topicmaps.ao.TopicAO;
import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.topicmaps.data.TopicBaseName;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: Anders Skar, Kantega AS
 * Date: May 3, 2007
 * Time: 11:47:47 AM
 */
public class TopiclistAttribute extends ListAttribute {
    private int topicMapId = -1;
    private String instanceOf = null;
    private Logger log = Logger.getLogger(getClass());

    public void setConfig(Element config, Map model) throws InvalidTemplateException, SystemException {
        super.setConfig(config, model);

        if (config != null) {
            String topicmapid = config.getAttribute("topicmapid");
            if (topicmapid != null && topicmapid.length() > 0) {
                this.topicMapId = Integer.parseInt(topicmapid, 10);
            }
            this.instanceOf = config.getAttribute("topicinstanceof");
        }
    }

    public boolean getMultiple() {
        return multiple;
    }

    public List getListOptions(int language) {
        List options = new ArrayList();

        List topics = null;

        try {
            if (instanceOf != null && instanceOf.length() > 0 && topicMapId != -1) {
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
            Log.error("TopiclistAttribute", e, null, null);
        }

        if (topics != null) {
            for (int i = 0; i < topics.size(); i++) {
                Topic topic =  (Topic)topics.get(i);
                ListOption option = new ListOption();
                option.setText(topic.getBaseName());
                option.setValue(topic.getTopicMapId() + ":" + topic.getId());
                options.add(option);
            }
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
        return topicMapId;
    }

    public String getInstanceOf() {
        return instanceOf;
    }

    public void addIndexFields(Document d) {
        if(getValue() != null) {
            String[] topics = getValue().split(",");
            for (int i = 0; i < topics.length; i++) {
                String[] topicStrings = topics[i].split(":");
                if(topicStrings.length == 2) {
                    try {
                        Topic topic = TopicAO.getTopic(Integer.parseInt(topicStrings[0]), topicStrings[1]);
                        if (topic != null) {
                            List basenames = topic.getBaseNames();
                            for (int j = 0; j < basenames.size(); j++) {
                                TopicBaseName baseName = (TopicBaseName) basenames.get(j);
                                d.add(new Field(Fields.TM_TOPICS, " " +baseName.getBaseName(), Field.Store.NO, Field.Index.ANALYZED));
                            }
                        } else {
                            log.debug("Fant ikke topic: " + topicStrings, null);
                        }
                    } catch (SystemException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
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
                Topic topic = new Topic(topicId, topicMapId);
                topicList.add(topic);
            }
        }
        return topicList;
    }
}
