package no.kantega.publishing.common;

import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.common.data.Content;

import java.util.Comparator;
import java.util.Map;
import java.util.HashMap;

/**
 *
 */
public class ContentTopicsRelevanceComparator implements Comparator<Content> {

    Map<String, Topic> topics;

    public ContentTopicsRelevanceComparator(Content content) {
        if (content != null && content.getTopics() != null) {
            topics = new HashMap<String, Topic>();
            for (Topic topic : content.getTopics()) {
                topics.put(topic.getId(), topic);
            }
        }
    }


    public int compare(Content content1, Content content2) {
        int match1 = getSimilarTopicsCount(content1);
        int match2 = getSimilarTopicsCount(content2);
        if (match2 > match1) {
            return 1;
        } else if (match2 < match1) {
            return -1;
        }
        return 0;
    }

    private int getSimilarTopicsCount(Content content) {
        int match = 0;
        if (content != null && content.getTopics() != null && content.getTopics().size() > 0) {
            for (Topic topic : content.getTopics()) {
                if (topics.get(topic.getId()) != null) {
                    match++;
                }
            }
        }

        return match;
    }
}

