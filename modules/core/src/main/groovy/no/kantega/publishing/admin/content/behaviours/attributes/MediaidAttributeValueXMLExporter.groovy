package no.kantega.publishing.admin.content.behaviours.attributes
import groovy.xml.MarkupBuilder
import no.kantega.publishing.common.data.Multimedia
import no.kantega.publishing.common.data.attributes.Attribute
import no.kantega.publishing.common.data.attributes.MediaidAttribute
import no.kantega.publishing.common.service.MultimediaService

class MediaidAttributeValueXMLExporter implements XMLAttributeValueExporter {
    String getAttributeValueAsXMLFragment(Attribute attr) {
        MediaidAttribute attribute = (MediaidAttribute) attr;
        def writer = new StringWriter();
        def xml = new MarkupBuilder(writer)
        xml.attribute(type: attribute.getClass().getSimpleName(), name: attribute.getName()){
            if (attribute.getValue() != null && attribute.getValue().trim().length() > 0) {
                MultimediaService mms = new MultimediaService();
                String[] values = attribute.getValue().split(",");
                for (int i = 0; i < values.length; i++) {
                    Multimedia mm = mms.getMultimedia(Integer.valueOf(values[i]));
                    media(id: values[i]){
                        name(mm.getName());
                        filename(mm.getFilename());
                        url(mm.getUrl());
                        description(mm.getAltname());
                        copyright(mm.getAuthor());
                        mimetype(mm.getMimeType());
                    }
                }
            }
        };
        return writer.toString();
    }
}
