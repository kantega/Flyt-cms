package no.kantega.publishing.admin.content.behaviours.attributes
import groovy.xml.MarkupBuilder
import no.kantega.publishing.common.data.attributes.Attribute
import no.kantega.publishing.common.data.enums.AttributeProperty

class HtmlAttributeValueXMLExporter implements XMLAttributeValueExporter {
    String getAttributeValueAsXMLFragment(Attribute attribute) {
        StringBuilder builder = new StringBuilder();
        builder.append("<![CDATA[");
        builder.append(attribute.getProperty(AttributeProperty.HTML));
        builder.append("]]>");
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        xml.attribute(type: attribute.getClass().getSimpleName(), name: attribute.getName(), builder.toString());
        return writer.toString();
    }
}
