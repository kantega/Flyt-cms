package no.kantega.openaksess.rest.representation;

import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.MediaAttribute;
import no.kantega.publishing.common.data.attributes.RepeaterAttribute;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlAccessorType(XmlAccessType.NONE)
public class AttributeTransferObject {
    private Attribute attribute;

    public AttributeTransferObject(Attribute attribute){
        this.attribute = attribute;
    }

    @XmlElement
    public String getName(){
        return attribute.getName();
    }

    @XmlElement
    public String getTitle(){
        return attribute.getTitle();
    }

    @XmlElement
    public String getValue(){
        return attribute.getValue();
    }

    @XmlElement
    public MultimediaTransferObject getMultimedia(){
        if(attribute instanceof MediaAttribute){
            Multimedia multimedia = ((MediaAttribute)attribute).getMultimedia();
            if(multimedia != null){
                return new MultimediaTransferObject(multimedia);
            }
        }
        return null;
    }

    @XmlElement
    public List<Map<String, AttributeTransferObject>> getRows(){
        if(attribute instanceof RepeaterAttribute){
            RepeaterAttribute repeaterAttribute = (RepeaterAttribute)attribute;
            return convertRows(repeaterAttribute);
        }
        return null;
    }

    private List<Map<String, AttributeTransferObject>> convertRows(RepeaterAttribute repeaterAttribute){
        List<Map<String, AttributeTransferObject>> output = new ArrayList<>(repeaterAttribute.getNumberOfRows());

        for(List<Attribute> attributes : repeaterAttribute){
            Map<String, AttributeTransferObject> map = new HashMap<>(attributes.size());
            for(Attribute attribute : attributes){
                map.put(attribute.getName(), new AttributeTransferObject(attribute));
            }
            output.add(map);
        }
        return output;
    }
}
