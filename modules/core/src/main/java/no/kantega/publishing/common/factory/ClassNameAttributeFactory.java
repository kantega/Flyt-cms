package no.kantega.publishing.common.factory;

import no.kantega.publishing.common.data.attributes.Attribute;

public class ClassNameAttributeFactory implements AttributeFactory {
    public static final String ATTRIBUTE_CLASS_PATH = "no.kantega.publishing.common.data.attributes.";

    public Attribute newAttribute(String attributeType) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (attributeType == null) {
            attributeType = "Text";
        }
        attributeType = attributeType.substring(0, 1).toUpperCase() + attributeType.substring(1, attributeType.length()).toLowerCase();

        return (Attribute)Class.forName(ATTRIBUTE_CLASS_PATH + attributeType + "Attribute").newInstance();
    }
}
