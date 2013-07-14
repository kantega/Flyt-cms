package no.kantega.publishing.admin.content.behaviours.attributes;


import no.kantega.publishing.common.data.attributes.Attribute;

public interface XMLAttributeValueExporter {
    public String getAttributeValueAsXMLFragment(Attribute attribute);
}
