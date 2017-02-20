package no.kantega.openaksess.rest.representation;

import no.kantega.publishing.topicmaps.data.TopicMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;
import java.util.stream.Collectors;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class TopicMapTransferObject {

    private TopicMap topicMap;

    public TopicMapTransferObject(TopicMap topicMap) {
        this.topicMap = topicMap;
    }

    @XmlElement
    public int getId(){
        return topicMap.getId();
    }

    @XmlElement
    public int getSecurityId(){
        return topicMap.getSecurityId();
    }

    @XmlElement
    public String getName(){
        return topicMap.getName();
    }

    @XmlElement
    public String getUrl(){
        return topicMap.getUrl();
    }

    @XmlElement
    public String getDefaultTopicInstance(){
        return topicMap.getDefaultTopicInstance();
    }

    @XmlElement
    public boolean isEditable(){
        return topicMap.isEditable();
    }

    @XmlElement
    public List<TopicTransferObject> getTopicTypes(){
        return topicMap.getTopicTypes()!= null ?
                topicMap.getTopicTypes().stream().map(TopicTransferObject::new).collect(Collectors.toList())
                : null;
    }



}
