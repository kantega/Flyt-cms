package no.kantega.publishing.common.data.attributes.util;

import no.kantega.publishing.topicmaps.ao.TopicAO;
import no.kantega.publishing.topicmaps.data.Topic;

import java.util.ArrayList;
import java.util.List;

public class TopicAttributeValueParser {
    public static Topic getValueAsTopic(String value) {
        List<Topic> topics = getValueAsTopics(value);
        if (topics.size() == 0) {
            return null;
        } else {
            return topics.get(0);
        }
    }

    public static List<Topic> getValueAsTopics(String value) {
        List<Topic> topicList = new ArrayList<Topic>();

        if (value == null || value.indexOf("") == -1) {
            return topicList;
        }

        String[] topics = value.split(",");
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

    public static String getTopicId(String value) {
        Topic topic = getValueAsTopic(value);
        if (topic == null) {
            return null;
        } else {
            return topic.getId();
        }
    }

    public static int getTopicMapId(String value) {
        Topic topic = getValueAsTopic(value);
        if (topic == null) {
            return -1;
        } else {
            return topic.getTopicMapId();
        }
    }



}
