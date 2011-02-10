package no.kantega.publishing.multimedia.metadata.exif;

public class ExifMetadata {
    public static final String EXIF_DIRECTORY = "Exif";
    public static final String GPS_DIRECTORY = "GPS";

    public static final String EXIF_MAKE = "Make";
    public static final String EXIF_MODEL = "Model";
    public static final String EXIF_ORIGINAL_DATE = "Date/Time Original";

    public static final String GPS_LATITUDE_REF = "GPS Latitude Ref";
    public static final String GPS_LATITUDE = "GPS Latitude";
    public static final String GPS_LONGITUDE_REF = "GPS Longitude Ref";
    public static final String GPS_LONGITUDE = "GPS Longitude";

    private String directory;
    private String key;
    private String value;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ExifMetadata{" +
                "directory='" + directory + '\'' +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
