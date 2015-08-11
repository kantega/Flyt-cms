package no.kantega.openaksess.contentApi.transferObject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.Attribute;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom
 * @since 07.08.15
 */
public class ContentTransferObject {
    @JsonIgnore
    private Content content;
    private HttpServletRequest request;

    public ContentTransferObject(Content content, HttpServletRequest request){
        this.content = content;
        this.request = request;
    }

    public String getUrl(){
        return request.getContextPath() + content.getPath(); // TODO: Should return context path.
    }

    public String getAlias(){
        return content.getAlias();
    }

    public Map<String, AttributeTransferObject> getContentAttributes(){
        Map<String, Attribute> contentAttributes = content.getContentAttributes();
        Map<String, AttributeTransferObject> transferObjectMap = new HashMap<>(contentAttributes.size());

        for(Map.Entry<String, Attribute> entry : contentAttributes.entrySet()){
            transferObjectMap.put(entry.getKey(), new AttributeTransferObject(entry.getValue()));
        }
        return transferObjectMap;
    }

    public String getDescription(){
        return content.getDescription();
    }

    public String getTitle(){
        return content.getTitle();
    }

    public ContentIdentifier getContentIdentifier(){
        return content.getContentIdentifier();
    }
}
