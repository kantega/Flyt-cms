package no.kantega.publishing.multimedia.metadata;

import no.kantega.publishing.common.data.ExifMetadata;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.multimedia.metadata.exif.ExifMetadataExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class JpegImageMetadataExtractor implements MultimediaMetadataExtractor {
    private static final Logger log = LoggerFactory.getLogger(JpegImageMetadataExtractor.class);
    private ExifMetadataExtractor exifMetadataExtractor;

    //TODO: See if Drew Noakes library adds support for IPTC Coded Character Set.

    public boolean supportsMimeType(String mimeType) {
        return mimeType.equals("image/jpeg");
    }

    public Multimedia extractMetadata(Multimedia multimedia) {
        List<ExifMetadata> metadatas = exifMetadataExtractor.getMetadataForImage(multimedia);
        for (ExifMetadata metadata : metadatas) {
            String directory = metadata.getDirectory();
            switch (directory) {
                case ExifMetadata.EXIF_DIRECTORY:
                case ExifMetadata.EXIF_SUBDIRECTORY:
                    addExifMetadata(multimedia, metadata);
                    break;
                case ExifMetadata.GPS_DIRECTORY:
                    addExifGPSMetadata(multimedia, metadata);
                    break;
                case ExifMetadata.IPTC_DIRECTORY:
                    addIptcMetadata(multimedia, metadata);
                    break;
            }
        }
        multimedia.setExifMetadata(metadatas);
        return multimedia;
    }

    private void addIptcMetadata(Multimedia multimedia, ExifMetadata metadata) {
        if (metadata.getKey().equals(ExifMetadata.IPTC_COPYRIGHT) && (isBlank(multimedia.getAuthor()))) {
            multimedia.setAuthor(metadata.getValue());
        }
    }


    private void addExifGPSMetadata(Multimedia multimedia, ExifMetadata metadata) {
        switch (metadata.getKey()) {
            case ExifMetadata.GPS_LATITUDE_REF:
                multimedia.setGpsLatitudeRef(metadata.getValue());
                break;
            case ExifMetadata.GPS_LATITUDE:
                multimedia.setGpsLatitude(metadata.getValue());
                break;
            case ExifMetadata.GPS_LONGITUDE_REF:
                multimedia.setGpsLongitudeRef(metadata.getValue());
                break;
            case ExifMetadata.GPS_LONGITUDE:
                multimedia.setGpsLongitude(metadata.getValue());
                break;
        }
    }

    private void addExifMetadata(Multimedia multimedia, ExifMetadata metadata) {
        String key = metadata.getKey();
        if (key.equals(ExifMetadata.EXIF_COPYRIGHT) && (isBlank(multimedia.getAuthor()))) {
            multimedia.setAuthor(metadata.getValue());
        } else if (key.equals(ExifMetadata.EXIF_MAKE)) {
            multimedia.setCameraMake(metadata.getValue());
        } else if (key.equals(ExifMetadata.EXIF_MODEL)) {
            multimedia.setCameraModel(metadata.getValue());
        } else if (key.equals(ExifMetadata.EXIF_ORIGINAL_DATE)) {
            if (!metadata.getValue().startsWith("0000")) {
                DateFormat df = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                try {
                    multimedia.setOriginalDate(df.parse(metadata.getValue()));
                } catch (ParseException e) {
                    log.info( "Unable to parse date for image:" + multimedia.getName());
                }
            }
        }
    }

    public void setExifMetadataExtractor(ExifMetadataExtractor exifMetadataExtractor) {
        this.exifMetadataExtractor = exifMetadataExtractor;
    }
}
