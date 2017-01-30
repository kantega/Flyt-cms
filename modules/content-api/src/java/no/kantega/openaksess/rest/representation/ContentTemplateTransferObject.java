package no.kantega.openaksess.rest.representation;

import no.kantega.publishing.common.data.AssociationCategory;
import no.kantega.publishing.common.data.ContentTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@XmlAccessorType(XmlAccessType.NONE)
public class ContentTemplateTransferObject {
    private ContentTemplate contentTemplate;

    public ContentTemplateTransferObject(ContentTemplate contentTemplate) {
        this.contentTemplate = contentTemplate;
    }

    @XmlElement
    public int getId(){
        return this.contentTemplate.getId();
    }

    @XmlElement
    public String getName(){
        return this.contentTemplate.getName();
    }

    @XmlElement
    public List<Integer> getAllowedParentTemplateIds(){
        return this.contentTemplate.getAllowedParentTemplates()
                .stream()
                .map(ContentTemplate::getId)
                .collect(Collectors.toList());
    }

    @XmlElement
    public String getContentType(){
        return this.contentTemplate.getContentType().name();
    }

    @XmlElement
    public String getDocumentType(){
        return this.contentTemplate.getDocumentType().getName();
    }

    @XmlElement
    public String getHelpText(){
        return this.contentTemplate.getHelptext();
    }

    @XmlElement
    public String getPublicId(){
        return this.contentTemplate.getPublicId();
    }

    @XmlElement
    public List<String> getAssociationCategories(){
        return this.contentTemplate.getAssociationCategories()
                .stream()
                .map(AssociationCategory::getName)
                .collect(Collectors.toList());
    }

    @XmlElement
    public List<Map<String, String>> getAttributes(){
        List<Map<String, String>> attributes = new ArrayList<>();
        for (Element element : this.contentTemplate.getAttributeElements()) {
            NamedNodeMap elementattributes = ((Element) element).getAttributes();
            Map<String, String> attributeItems = new HashMap<>();
            for(int i = 0; i < elementattributes.getLength(); i++){
                Node item = elementattributes.item(i);
                attributeItems.put(item.getNodeName(), item.getNodeValue());
            }
            attributes.add(attributeItems);
        }
        return attributes;
    }
}
