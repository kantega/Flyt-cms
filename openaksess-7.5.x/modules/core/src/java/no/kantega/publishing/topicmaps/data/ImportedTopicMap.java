package no.kantega.publishing.topicmaps.data;

import java.util.Collections;
import java.util.List;

public class ImportedTopicMap {

    List<Topic> topicList;
    List<TopicAssociation> topicAssociationList;
    TopicMap topicMap;

    public ImportedTopicMap(TopicMap topicMap, List<Topic> topicList, List<TopicAssociation> topicAssociationList) {
        this.topicList = topicList;
        this.topicAssociationList = topicAssociationList;
        this.topicMap = topicMap;
    }

    public List<Topic> getTopicList() {
        if(topicList == null){
            return Collections.emptyList();
        }
        return topicList;
    }

    public List<TopicAssociation> getTopicAssociationList() {
        if(topicAssociationList == null){
            return Collections.emptyList();
        }
        return topicAssociationList;
    }

    public TopicMap getTopicMap() {
        return topicMap;
    }
}
