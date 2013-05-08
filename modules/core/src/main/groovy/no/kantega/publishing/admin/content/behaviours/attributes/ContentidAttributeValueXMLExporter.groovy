package no.kantega.publishing.admin.content.behaviours.attributes
import groovy.xml.MarkupBuilder
import no.kantega.publishing.common.data.ContentIdentifier
import no.kantega.publishing.common.data.attributes.Attribute
import no.kantega.publishing.common.data.attributes.ContentidAttribute

class ContentidAttributeValueXMLExporter implements XMLAttributeValueExporter {
    String getAttributeValueAsXMLFragment(Attribute attr) {
        ContentidAttribute attribute = (ContentidAttribute) attr;
        def writer = new StringWriter();
        def xml = new MarkupBuilder(writer)
        xml.attribute(type: attribute.getClass().getSimpleName(), name: attribute.getName()){
            if (attribute.getValue() != null && attribute.getValue().trim().length() > 0) {
                List<ContentIdentifier> contentidList = attribute.getValueAsContentIdentifiers();
                contentids(){
                    for (ContentIdentifier cid : contentidList) {
                        contentid(id:cid.getContentId(), association:cid.getAssociationId(), url:"/content/" + cid.getAssociationId() + "/");
                    }
                }
            }
        };
        return writer.toString();
    }
}
