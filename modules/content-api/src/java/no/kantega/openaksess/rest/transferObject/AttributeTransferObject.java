package no.kantega.openaksess.rest.transferObject;

import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.MediaAttribute;
import no.kantega.publishing.common.data.attributes.RepeaterAttribute;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    public List<List<AttributeTransferObject>> getRows(){
        if(attribute instanceof RepeaterAttribute){
            RepeaterAttribute repeaterAttribute = (RepeaterAttribute)attribute;
            return convertRows(repeaterAttribute);
        }
        return null;
    }

    private List<List<AttributeTransferObject>> convertRows(RepeaterAttribute repeaterAttribute){
        List<List<AttributeTransferObject>> output = new ArrayList<>(repeaterAttribute.getNumberOfRows());
        Iterator<List<Attribute>> iterator = repeaterAttribute.getIterator();
        while(iterator.hasNext()){
            List<Attribute> next = iterator.next();
            List<AttributeTransferObject> row = new ArrayList<>(next.size());
            for(Attribute attr : next){
                row.add(new AttributeTransferObject(attr));
            }
            output.add(row);
        }

        return output;
    }
}
