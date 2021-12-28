package no.kantega.publishing.common.data.attributes.util;

import no.kantega.publishing.spring.RootContext;
import no.kantega.publishing.topicmaps.ao.TopicDao;
import no.kantega.publishing.topicmaps.data.Topic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class TopicAttributeValueParser {
    private static TopicDao topicDao;

    public static Topic getValueAsTopic(String value) {
        List<Topic> topics = getValueAsTopics(value);
        if (topics.size() == 0) {
            return null;
        } else {
            return topics.get(0);
        }
    }

    public static List<Topic> getValueAsTopics(String value) {
        if (isBlank(value)) {
            return Collections.emptyList();
        }

        initDao();

        String[] topics = value.split(",");
        List<Topic> topicList = new ArrayList<>(topics.length);

        for (String topic1 : topics) {
            String[] topicStrings = topic1.split(":");
            if (topicStrings.length == 2) {
                int topicMapId = Integer.parseInt(topicStrings[0]);
                String topicId = topicStrings[1];
                Topic topic = topicDao.getTopic(topicMapId, topicId);
                if (topic != null) {
                    topicList.add(topic);
                }
            }
        }
        return topicList;
    }

    private static void initDao() {
        if (topicDao == null) {
            topicDao = RootContext.getInstance().getBean(TopicDao.class);
        }
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
