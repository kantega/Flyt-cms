package no.kantega.publishing.multimedia.metadata;

import no.kantega.commons.log.Log;
import no.kantega.publishing.common.data.ExifMetadata;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.multimedia.metadata.exif.ExifMetadataExtractor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class JpegImageMetadataExtractor implements MultimediaMetadataExtractor {
    private ExifMetadataExtractor exifMetadataExtractor;

    public boolean supportsMimeType(String mimeType) {
        return mimeType.equals("image/jpeg");
    }

    public Multimedia extractMetadata(Multimedia multimedia) {
        List<ExifMetadata> metadatas = exifMetadataExtractor.getMetadataForImage(multimedia.getData());
        for (ExifMetadata metadata : metadatas) {
            if (metadata.getDirectory().equals(ExifMetadata.EXIF_DIRECTORY)) {
                addExifMetadata(multimedia, metadata);
            } else if (metadata.getDirectory().equals(ExifMetadata.GPS_DIRECTORY)) {
                addExifGPSMetadata(multimedia, metadata);
            }
        }
        multimedia.setExifMetadata(metadatas);
        return multimedia;
    }


    private void addExifGPSMetadata(Multimedia multimedia, ExifMetadata metadata) {
        if (metadata.getKey().equals(ExifMetadata.GPS_LATITUDE_REF)) {
            multimedia.setGpsLatitudeRef(metadata.getValue());
        } else if (metadata.getKey().equals(ExifMetadata.GPS_LATITUDE)) {
            multimedia.setGpsLatitude(metadata.getValue());
        } else if (metadata.getKey().equals(ExifMetadata.GPS_LONGITUDE_REF)) {
            multimedia.setGpsLongitudeRef(metadata.getValue());
        } else if (metadata.getKey().equals(ExifMetadata.GPS_LONGITUDE)) {
            multimedia.setGpsLongitude(metadata.getValue());
        }
    }

    private void addExifMetadata(Multimedia multimedia, ExifMetadata metadata) {
        if (metadata.getKey().equals(ExifMetadata.EXIF_MAKE)) {
            multimedia.setCameraMake(metadata.getValue());
        } else if (metadata.getKey().equals(ExifMetadata.EXIF_MODEL)) {
            multimedia.setCameraModel(metadata.getValue());
        } else if (metadata.getKey().equals(ExifMetadata.EXIF_ORIGINAL_DATE)) {
            DateFormat df = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            try {
                multimedia.setOriginalDate(df.parse(metadata.getValue()));
            } catch (ParseException e) {
                Log.info(this.getClass().getName(), "Unable to parse date for image:" + multimedia.getName());
            }
        }
    }

    public void setExifMetadataExtractor(ExifMetadataExtractor exifMetadataExtractor) {
        this.exifMetadataExtractor = exifMetadataExtractor;
    }
}
