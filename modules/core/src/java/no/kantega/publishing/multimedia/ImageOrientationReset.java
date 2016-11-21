package no.kantega.publishing.multimedia;


import no.kantega.publishing.common.data.ExifMetadata;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.exception.InvalidImageFormatException;
import no.kantega.publishing.multimedia.metadata.exif.ExifMetadataExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class ImageOrientationReset {
    private static final Logger log = LoggerFactory.getLogger(ImageOrientationReset.class);
    private ImageEditor imageEditor;
    private ExifMetadataExtractor exifMetadataExtractor;

    public static final String ORIENTATION_VALUE_1 = "1";
    public static final String ORIENTATION_VALUE_3 = "3";
    public static final String ORIENTATION_VALUE_6 = "6";
    public static final String ORIENTATION_VALUE_8 = "8";

    public Multimedia resetOrientation(Multimedia multimedia) {
        try {
            List<ExifMetadata> metadatas = exifMetadataExtractor.getMetadataForImage(multimedia);
            int degreesToRotate = 0;
            for (ExifMetadata metadata : metadatas) {
                String directory = metadata.getDirectory();
                if (directory.equals(ExifMetadata.EXIF_DIRECTORY) || directory.equals(ExifMetadata.EXIF_SUBDIRECTORY)) {
                    if (metadata.getKey().equals(ExifMetadata.EXIF_ORIENTATION)) {
                        String orientationValue = metadata.getValue();
                        switch (orientationValue) {
                            case ORIENTATION_VALUE_1:
                                degreesToRotate = 0;
                                break;
                            case ORIENTATION_VALUE_3:
                                degreesToRotate = 180;
                                break;
                            case ORIENTATION_VALUE_6:
                                degreesToRotate = 90;
                                break;
                            case ORIENTATION_VALUE_8:
                                degreesToRotate = -90;
                                break;
                        }
                    }
                }
            }
            if (Math.abs(degreesToRotate) == 90) {
                return imageEditor.rotateMultimedia(multimedia, degreesToRotate);
            } else if (degreesToRotate == 180) {
                multimedia = imageEditor.rotateMultimedia(multimedia, degreesToRotate/2);
                return imageEditor.rotateMultimedia(multimedia, degreesToRotate/2);
            }
        } catch (IOException e) {
            log.info( "Error occurred during image orientation reset. Image id: " + multimedia.getId());
        } catch (InvalidImageFormatException e) {
            log.info( "Invalid image formatduring image orientation reset. Image id: " + multimedia.getId());
        }
        return multimedia;
    }

    public void setImageEditor(ImageEditor imageEditor) {
        this.imageEditor = imageEditor;
    }

    public void setExifMetadataExtractor(ExifMetadataExtractor exifMetadataExtractor) {
        this.exifMetadataExtractor = exifMetadataExtractor;
    }
}
