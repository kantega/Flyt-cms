package no.kantega.publishing.common.factory;

import no.kantega.publishing.common.data.attributes.Attribute;

public interface AttributeFactory {
    public Attribute newAttribute(String attributeType) throws ClassNotFoundException, InstantiationException, IllegalAccessException;
}
