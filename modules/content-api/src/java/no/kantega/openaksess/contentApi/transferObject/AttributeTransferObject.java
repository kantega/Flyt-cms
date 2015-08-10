package no.kantega.openaksess.contentApi.transferObject;

import com.fasterxml.jackson.annotation.JsonInclude;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.MediaAttribute;
import no.kantega.publishing.common.data.attributes.RepeaterAttribute;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Tom
 * @since 07.08.15
 */
public class AttributeTransferObject {
    private Attribute attribute;

    public AttributeTransferObject(Attribute attribute){
        this.attribute = attribute;
    }

    public String getName(){
        return attribute.getName();
    }

    public String getTitle(){
        return attribute.getTitle();
    }

    public String getValue(){
        return attribute.getValue();
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public MultimediaTransferObject getMultimedia(){
        if(attribute instanceof MediaAttribute){
            return new MultimediaTransferObject(((MediaAttribute)attribute).getMultimedia());
        }
        return null;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
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
