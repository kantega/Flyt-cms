package no.kantega.publishing.admin.content.behaviours.attributes
import groovy.xml.MarkupBuilder
import no.kantega.publishing.common.data.attributes.Attribute
import no.kantega.publishing.common.data.attributes.ImageAttribute
import no.kantega.publishing.common.data.enums.AttributeProperty

class ImageAttributeValueXMLExporter implements XMLAttributeValueExporter {
    String getAttributeValueAsXMLFragment(Attribute attr) {
        ImageAttribute attribute = (ImageAttribute) attr;
        def writer = new StringWriter();
        def xml = new MarkupBuilder(writer)
        xml.attribute(type: attribute.getClass().getSimpleName(), name: attribute.getName()){
            if (attribute.getValue() != null && attribute.getValue().trim().length() > 0) {
                id(attribute.getValue());
                filename(attribute.getProperty(AttributeProperty.NAME));
                url(attribute.getProperty(AttributeProperty.URL));
                description(attribute.getProperty(AttributeProperty.ALTNAME));
                copyright(attribute.getProperty(AttributeProperty.AUTHOR));
                mimetype(attribute.getProperty(AttributeProperty.MIMETYPE));
            }
        };
        return writer.toString();
    }
}
