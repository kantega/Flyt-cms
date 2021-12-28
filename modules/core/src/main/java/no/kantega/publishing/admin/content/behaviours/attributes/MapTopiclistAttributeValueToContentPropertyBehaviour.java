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
import no.kantega.publishing.common.data.attributes.TopiclistAttribute;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.spring.RootContext;
import no.kantega.publishing.topicmaps.ao.TopicDao;
import no.kantega.publishing.topicmaps.data.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

public class MapTopiclistAttributeValueToContentPropertyBehaviour implements MapAttributeValueToContentPropertyBehaviour {
    private static final Logger log = LoggerFactory.getLogger(MapTopiclistAttributeValueToContentPropertyBehaviour.class);

    private static TopicDao topicDao;

    public void mapAttributeValue(RequestParameters param, Content content, Attribute attribute, String field, ValidationErrors errors) {
        if(topicDao == null){
            topicDao = RootContext.getInstance().getBean(TopicDao.class);
        }

        if (field != null) {
            if (field.equalsIgnoreCase(ContentProperty.TOPICS)) {
                TopiclistAttribute topicsAttr = (TopiclistAttribute)attribute;

                int topicMapId = topicsAttr.getTopicMapId();
                String instanceOf = topicsAttr.getInstanceOf();
                if (instanceOf == null) {
                    instanceOf = "";
                }

                // Fjern eksisterende topics
                List<Topic> topics = content.getTopics();
                Iterator<Topic> it = topics.iterator();
                while (it.hasNext()) {
                    Topic t = it.next();
                    if (t.getTopicMapId() == topicMapId && (t.getInstanceOf().getId().equals(instanceOf) || instanceOf.length() == 0)) {
                        it.remove();
                    }
                }

                // Legg til valgte topics
                String value = topicsAttr.getValue();
                if (value != null && value.length() > 0) {
                    String[] newTopics = value.split(",");

                    for (String topicStr : newTopics) {
                        if (topicStr.contains(":")) {
                            String strTopicMapId = topicStr.substring(0, topicStr.indexOf(":"));
                            String topicId = topicStr.substring(topicStr.indexOf(":") + 1, topicStr.length());
                            Topic t = null;
                            t = topicDao.getTopic(Integer.parseInt(strTopicMapId), topicId);

                            if (t != null) {
                                topics.add(t);
                            }
                        }
                    }
                }
            }
        }
    }
}
