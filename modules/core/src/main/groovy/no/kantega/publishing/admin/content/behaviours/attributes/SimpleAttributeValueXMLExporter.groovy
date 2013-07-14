package no.kantega.publishing.admin.content.behaviours.attributes

import groovy.xml.MarkupBuilder
import no.kantega.publishing.common.data.attributes.Attribute

class SimpleAttributeValueXMLExporter implements XMLAttributeValueExporter {
    String getAttributeValueAsXMLFragment(Attribute attribute) {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        xml.attribute(type: attribute.getClass().getSimpleName(), name: attribute.getName(), attribute.getValue())
        return writer.toString();
    }
}
