package no.kantega.publishing.api.content.attribute;

public enum AttributeDataType {
    ANY(-1), CONTENT_DATA(0),META_DATA(1);

    private int attributeDataTypeAsId;

    AttributeDataType(int attributeDataTypeAsId){
        this.attributeDataTypeAsId= attributeDataTypeAsId;
    }

    public int getDataTypeAsId() {
        return attributeDataTypeAsId;
    }

    public static AttributeDataType getDataTypeAsEnum(int typeAsInt) {
        for (AttributeDataType type : AttributeDataType.values()) {
            if (type.getDataTypeAsId() == typeAsInt) {
                return type;
            }
        }

        return AttributeDataType.ANY;
    }
}
