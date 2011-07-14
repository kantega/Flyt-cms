package no.kantega.publishing.modules.forms.filter;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

import java.util.ArrayList;
import java.util.List;

public class GetFormFieldsFilter extends XMLFilterImpl {
    private List<String> fieldNames =  new ArrayList<String>();

    @Override
    public void startElement(String string, String localName, String name, Attributes attributes) throws SAXException {
        if (name.equalsIgnoreCase("input") || name.equalsIgnoreCase("select") || name.equalsIgnoreCase("textarea")) {
            String inputName = attributes.getValue("name");
            fieldNames.add(inputName);
        }
        super.startElement(string,  localName, name, attributes);
    }

    public List<String> getFieldNames() {
        return fieldNames;
    }
}
