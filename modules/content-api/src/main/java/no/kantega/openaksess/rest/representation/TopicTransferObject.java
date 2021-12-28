package no.kantega.openaksess.rest.representation;

import no.kantega.publishing.topicmaps.data.Topic;
import no.kantega.publishing.topicmaps.data.TopicOccurence;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class TopicTransferObject {
    Topic topic;

    public TopicTransferObject(Topic topic) {
        this.topic = topic;
    }

    @XmlElement
    public String getId(){
        return topic.getId();
    }

    @XmlElement
    public String getBaseName(){
        return topic.getBaseName();
    }

    @XmlElement
    public List<String> getOccurances() {
        if (topic.getOccurences() != null) {
            return topic.getOccurences().stream().map(TopicOccurence::getResourceData).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @XmlElement
    public TopicTransferObject getInstanceOf(){
        if(topic.getInstanceOf()!=null) {
            return new TopicTransferObject(topic.getInstanceOf());
        }
        return null;
    }

    @XmlElement
    public int getNoUsages(){
        return topic.getNoUsages();
    }

    @XmlElement
    public String getSubjectIdentity(){
        return topic.getSubjectIdentity();
    }
}
