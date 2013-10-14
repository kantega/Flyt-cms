package no.kantega.publishing.common.data.enums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Cropping {
    CONTAIN("contain"),
    TOPLEFT("topleft"),
    CENTERED("centered"),
    TOPCENTER("topcenter");

    private String typeAsString;
    private static final Logger log = LoggerFactory.getLogger(Cropping.class);

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
            log.error( "Type cannot be translated to valid enumeration.", e);
        }
        return Cropping.CONTAIN;
    }
}
