package no.kantega.publishing.common.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExifMetadata {
    public static final String EXIF_DIRECTORY = "Exif IFD0";
    public static final String EXIF_SUBDIRECTORY = "Exif SubIFD";
    public static final String IPTC_DIRECTORY = "Iptc";
    public static final String GPS_DIRECTORY = "GPS";

    public static final String EXIF_MAKE = "Make";
    public static final String EXIF_MODEL = "Model";
    public static final String EXIF_ORIGINAL_DATE = "Date/Time Original";
    public static final String EXIF_COPYRIGHT = "Copyright";
    public static final String IPTC_COPYRIGHT = "Copyright Notice";
    public static final String GPS_LATITUDE_REF = "GPS Latitude Ref";
    public static final String GPS_LATITUDE = "GPS Latitude";
    public static final String GPS_LONGITUDE_REF = "GPS Longitude Ref";
    public static final String GPS_LONGITUDE = "GPS Longitude";

    private String directory;
    private String key;
    private String[] values;

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String[] getValues() {
        if (values == null) {
            return new String[]{};
        } else {
            return values;
        }
    }

    public void setValues(String[] values) {
        this.values = values;
    }

    public String getValue() {
        if (values == null) {
            return null;
        } else {
            return values[0];
        }
    }

    public void setValue(String value) {
        this.values = new String[] {value};
    }

    public void addValue(String value) {
        List<String> listValues = new ArrayList<String>(Arrays.asList(getValues()));
        listValues.add(value);
        setValues(listValues.toArray(new String[listValues.size()]));
    }

    @Override
    public String toString() {
        return "ExifMetadata{" +
                "directory='" + directory + '\'' +
                ", key='" + key + '\'' +
                ", values=" + (values == null ? null : Arrays.asList(values)) +
                '}';
    }

}
