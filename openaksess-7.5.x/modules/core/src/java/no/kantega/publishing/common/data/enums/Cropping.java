package no.kantega.publishing.common.data.enums;

import no.kantega.commons.log.Log;

public enum Cropping {
    CONTAIN("contain"),
    TOPLEFT("topleft"),
    CENTERED("centered");

    private String typeAsString;
    private static final String objectName = "no.kantega.publishing.common.data.enums.Cropping";

    Cropping(String type) {
        this.typeAsString = type;
    }

    public String getTypeAsString() {
        return typeAsString;
    }

    public static Cropping getCroppingAsEnum(String typeAsString) {
        try{
            for (Cropping type : Cropping.values()) {
                if (type.getTypeAsString().toLowerCase().equals(typeAsString.toLowerCase())) {
                    return type;
                }
            }
        } catch (Exception e){
            Log.error(objectName, "Type cannot be translated to valid enumeration.");
        }
        return Cropping.CONTAIN;
    }
}
