package no.kantega.openaksess.rest.representation;

import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.Attribute;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;

@XmlRootElement(name = "content")
@XmlAccessorType(XmlAccessType.NONE)
public class ContentTransferObject {
    private Content content;
    private HttpServletRequest request;

    public ContentTransferObject(Content content, HttpServletRequest request){
        this.content = content;
        this.request = request;
    }

    @XmlElement
    public String getUrl(){
        return request.getContextPath() + content.getPath(); // TODO: Should return context path.
    }

    @XmlElement
    public String getAlias(){
        return content.getAlias();
    }

    @XmlElement
    public Map<String, AttributeTransferObject> getContentAttributes(){
        Map<String, Attribute> contentAttributes = content.getContentAttributes();
        Map<String, AttributeTransferObject> transferObjectMap = new HashMap<>(contentAttributes.size());

        for(Map.Entry<String, Attribute> entry : contentAttributes.entrySet()){
            transferObjectMap.put(entry.getKey(), new AttributeTransferObject(entry.getValue()));
        }
        return transferObjectMap;
    }

    @XmlElement
    public String getDescription(){
        return content.getDescription();
    }

    @XmlElement
    public String getTitle(){
        return content.getTitle();
    }

    @XmlElement
    public ContentIdentifier getContentIdentifier(){
        return content.getContentIdentifier();
    }
}
