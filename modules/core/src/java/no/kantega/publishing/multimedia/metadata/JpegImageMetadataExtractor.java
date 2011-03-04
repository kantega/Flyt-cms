package no.kantega.publishing.multimedia.metadata;

import no.kantega.commons.log.Log;
import no.kantega.publishing.common.data.ExifMetadata;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.multimedia.metadata.exif.ExifMetadataExtractor;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class JpegImageMetadataExtractor implements MultimediaMetadataExtractor {
    private ExifMetadataExtractor exifMetadataExtractor;

    /* Assume UTF-8 for IPTC. */
    //TODO: See if Drew Noakes libs add support for IPTC Coded Character Set.
    private static final String iptcEncoding = "UTF-8";

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
            } else if (metadata.getDirectory().equals(ExifMetadata.IPTC_DIRECTORY)) {
                addIptcMetadata(multimedia, metadata);
            }
        }
        multimedia.setExifMetadata(metadatas);
        return multimedia;
    }

    private void addIptcMetadata(Multimedia multimedia, ExifMetadata metadata) {
        if (metadata.getKey().equals(ExifMetadata.IPTC_COPYRIGHT) && (multimedia.getAuthor() == null || multimedia.getAuthor().length() == 0 )) {
            multimedia.setAuthor(metadata.getValue());
        }
        else if (metadata.getKey().equals(ExifMetadata.IPTC_KEYWORDS) && (multimedia.getDescription() == null || multimedia.getDescription().length() == 0 )) {
            StringBuffer buf = new StringBuffer();
            String[] keywords = metadata.getValues();
            for (int i = 0; i<keywords.length; i++) {
                try {
                    buf.append(new String(keywords[i].getBytes(), iptcEncoding));
                    if (i!=keywords.length-1) {
                        buf.append(", ");
                    }
                }
                catch (UnsupportedEncodingException e) {
                    Log.error(this.getClass().getName(), e);
                }
            }
            multimedia.setDescription(buf.toString());
        }
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
        if (metadata.getKey().equals(ExifMetadata.EXIF_COPYRIGHT) && (multimedia.getAuthor() == null || multimedia.getAuthor().length() == 0)) {
            multimedia.setAuthor(metadata.getValue());
        } else if (metadata.getKey().equals(ExifMetadata.EXIF_MAKE)) {
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
