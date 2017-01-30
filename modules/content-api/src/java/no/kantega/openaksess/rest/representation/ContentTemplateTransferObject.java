package no.kantega.openaksess.rest.representation;

import no.kantega.publishing.common.data.ContentTemplate;
import no.kantega.publishing.common.data.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
        DocumentType documentType = contentTemplate.getDocumentType();
        if(documentType!=null){
            return documentType.getName();
        }
        return "No type";
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
    public List<Map<String, Object>> getAttributes(){
        List<Map<String, Object>> attributes = new ArrayList<>();
        for (Element element : this.contentTemplate.getAttributeElements()) {
            attributes.add(createAttributeTransferElement(element));
        }
        return attributes;
    }

    // Attribute helper methods
    private  Map<String, Object> createAttributeTransferElement(Element element){
        NamedNodeMap elementattributes = ((Element) element).getAttributes();
        Map<String, Object> attributeItems = new HashMap<>();
        for(int i = 0; i < elementattributes.getLength(); i++){
            Node item = elementattributes.item(i);
            attributeItems.put(item.getNodeName(), item.getNodeValue());
        }
        attributeItems.put("attributeType", element.getTagName());
        if(element.getTagName().equals("repeater")){
            attributeItems.put("repeaterAttributes", getRepeaterChildren(element));
        }
        return attributeItems;
    }

    private List<Map<String, String>> getRepeaterChildren(Element element){
        List<Map<String, String>> repeaterAttributes = new ArrayList<>();
        NodeList children = ((Element) element).getChildNodes();
        for (int i = 0; i < children.getLength(); i++){
            Node child = children.item(i);
            if(child.getNodeName().equals("attribute")){
                // put attribute object in list
                NamedNodeMap attributeMap = child.getAttributes();
                Map<String, String> attr = new HashMap<>();
                for(int j = 0; j < attributeMap.getLength(); j++){
                    attr.put(attributeMap.item(j).getNodeName(), attributeMap.item(j).getNodeValue());
                }
                repeaterAttributes.add(attr);
            }
        }
        return repeaterAttributes;
    }



}


