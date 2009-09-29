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
import no.kantega.publishing.topicmaps.data.Topic;

import java.text.ParseException;

public class TopicAttribute  extends Attribute {

    public String getRenderer() {
        return "topic";
    }

    public Topic getValueAsTopic() {
        if (value == null || value.indexOf(":") == -1) {
            return null;
        }

        String topicMapId = value.substring(0, value.indexOf(":"));
        String topicId = value.substring(value.indexOf(":") + 1, value.length());
        try {
            return new Topic(topicId, Integer.parseInt(topicMapId));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public String getTopicId() {
        if (value == null || value.indexOf(":") == -1) {
            return null;
        }

        return  value.substring(value.indexOf(":") + 1, value.length());
    }

    public int getTopicMapId() {
        if (value == null || value.indexOf(":") == -1) {
            return -1;
        }

        String topicMapId = value.substring(0, value.indexOf(":"));
        try {
            return Integer.parseInt(topicMapId);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}

