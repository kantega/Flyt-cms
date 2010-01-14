package no.kantega.publishing.modules.forms.filter;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;
import org.springframework.web.util.HtmlUtils;

import java.util.Map;
import java.util.List;

import no.kantega.publishing.modules.forms.validate.FormError;

/**
 *  Prefills HTML form with values from hashmap
 *
 *  NOTE! This filter cannot be run twice in a row
 */
public class FormFillFilter extends XMLFilterImpl {
    private Map<String, String[]> params;
    private List<FormError> errors;
    private String currentSelectList = null;
    private int currentFieldIndex;

    public FormFillFilter(Map<String, String[]> params, List<FormError> errors) {
        this.params = params;
        this.errors = errors;
        this.currentFieldIndex = 0;
    }

    @Override
    public void startElement(String string, String localName, String name, Attributes attributes) throws SAXException {
        String inputName = attributes.getValue("name");
        String inputType = attributes.getValue("type");

        if (name.equalsIgnoreCase("div")) {
            if (attributes != null && attributes.getValue("class") != null && attributes.getValue("class").contains("formElement")) {
                currentFieldIndex++;
                boolean hasError = false;
                for (FormError error : errors) {
                    if (error.getIndex() == currentFieldIndex) {
                        hasError = true;
                        break;
                    }
                }
                if (hasError) {
                    AttributesImpl newAttributes = new AttributesImpl(attributes);
                    int inx = newAttributes.getIndex("", "class");
                    String clz = newAttributes.getValue("class");
                    newAttributes.setAttribute(inx, "", "class", "class", "CDATA", clz + " error");
                    attributes = newAttributes;
                }
            }
        }

        if (name.equalsIgnoreCase("input")) {
            if ("text".equals(inputType) || "hidden".equals(inputType)) {
                String[] values = params.get(inputName);
                String value = null;
                if (values != null && values.length > 0) {
                    value = values[0];
                }
                if (value != null) {
                    value = HtmlUtils.htmlEscape(value);
                    AttributesImpl newAttributes = new AttributesImpl(attributes);
                    int inx = newAttributes.getIndex("", "value");
                    if (inx == -1) {
                        newAttributes.addAttribute("", "value", "value", "CDATA", value);
                    } else {
                        newAttributes.setAttribute(inx, "", "value", "value", "CDATA", value);
                    }                    
                    attributes = newAttributes;
                }
                               
            } else if ("radio".equals(inputType) || "checkbox".equals(inputType)) {
                String inputValue = attributes.getValue("value");
                String[] values = params.get(inputName);
                String value = "";
                if (inputValue != null && values != null) {
                    for (String v : values) {
                        if (inputValue.equals(v)) {
                            value = v;
                            break;
                        }
                    }
                }
                AttributesImpl newAttributes = new AttributesImpl();
                newAttributes.addAttribute("", "type", "type", "CDATA", inputType);
                newAttributes.addAttribute("", "name", "name", "CDATA", inputName);
                newAttributes.addAttribute("", "value", "value", "CDATA", inputValue);
                if (value.equals(inputValue)) {
                    newAttributes.addAttribute("", "checked", "checked", "CDATA", "checked");
                }
                attributes = newAttributes;
            }
        } else if ("select".equals(name)) {
            // Add the name of the select list
            currentSelectList = attributes.getValue("name");
        } else if ("option".equals(name)) {
            if (currentSelectList != null) {
                String inputValue = attributes.getValue("value");
                String[] values = params.get(currentSelectList);
                String value = null;
                if (values != null && values.length > 0) {
                    value = values[0];
                }
                if (value != null) {
                    AttributesImpl newAttributes = new AttributesImpl(attributes);
                    newAttributes.addAttribute("", "value", "value", "CDATA", inputValue);
                    if (value.equals(inputValue)) {
                        newAttributes.addAttribute("", "selected", "selected", "CDATA", "selected");
                    }
                    attributes = newAttributes;
                }
            }
        }

        super.startElement(string,  localName, name, attributes);

        if ("textarea".equals(name) && inputName != null) {
            // Add value in between textarea start and end
            String[] values = params.get(inputName);
            String value = null;
            if (values != null && values.length > 0) {
                value = values[0];
            }
            if (value != null) {
                value = HtmlUtils.htmlEscape(value);
                super.characters(value.toCharArray(), 0, value.length());
            }
        }
    }

    @Override
    public void endElement(String string, String localname, String name) throws SAXException {
        if ("select".equals(name) && "option".equals(name) && "optgroup".equals(name)) {
            currentSelectList = null;
        }
        super.endElement(string, localname, name);
    }
}
