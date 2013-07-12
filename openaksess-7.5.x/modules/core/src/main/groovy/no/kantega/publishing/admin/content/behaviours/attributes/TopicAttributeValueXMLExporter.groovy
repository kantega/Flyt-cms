package no.kantega.publishing.admin.content.behaviours.attributes
import groovy.xml.MarkupBuilder
import no.kantega.publishing.common.data.attributes.Attribute
import no.kantega.publishing.common.data.attributes.TopicAttribute

class TopicAttributeValueXMLExporter implements XMLAttributeValueExporter {
    String getAttributeValueAsXMLFragment(Attribute attr) {
        TopicAttribute attribute = (TopicAttribute) attr;
        def writer = new StringWriter();
        def xml = new MarkupBuilder(writer)
        xml.attribute(type: attribute.getClass().getSimpleName(), name: attribute.getName()){
            if (attribute.getValue() != null && attribute.getValue().trim().length() > 0) {
                topic(topicid:attribute.getTopicId(), topicmapid: attribute.getTopicMapId());
            }
        };
        return writer.toString();
    }
}
