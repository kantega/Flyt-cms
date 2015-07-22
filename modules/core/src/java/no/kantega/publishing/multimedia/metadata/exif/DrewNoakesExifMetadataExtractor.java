package no.kantega.publishing.multimedia.metadata.exif;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import no.kantega.publishing.common.data.ExifMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DrewNoakesExifMetadataExtractor implements ExifMetadataExtractor {
    private static final Logger log = LoggerFactory.getLogger(DrewNoakesExifMetadataExtractor.class);

    public List<ExifMetadata> getMetadataForImage(byte[] imageData) {
        List<ExifMetadata> exifMetadatas = new ArrayList<>();

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData)){
            exifMetadatas = extractMetadata(inputStream);
        } catch (IOException | JpegProcessingException e) {
            log.error("", e);
        }

        return exifMetadatas;
    }

    private List<ExifMetadata> extractMetadata(ByteArrayInputStream inputStream) throws JpegProcessingException {
        List<ExifMetadata> exifMetadatas = new ArrayList<>();

        Metadata metadata = JpegMetadataReader.readMetadata(inputStream);

        for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                try {
                    ExifMetadata exifMetadata = getMetadataFromTag(directory, tag);
                    if (exifMetadata != null) {
                        exifMetadatas.add(exifMetadata);
                    }

                } catch (MetadataException e) {
                    log.error("", e);
                }
            }
        }

        return exifMetadatas;
    }

    private ExifMetadata getMetadataFromTag(Directory directory, Tag tag) throws MetadataException {
        ExifMetadata metadata = new ExifMetadata();
        metadata.setDirectory(tag.getDirectoryName());
        metadata.setKey(tag.getTagName());
        metadata.setValue(tag.getDescription());

        String values[];
        Object value = directory.getObject(tag.getTagType());
        if (value instanceof byte[]) {
            return null;
        } else if (value instanceof String) {
            values = new String[] {(String)value};
        } else if (value instanceof String[]) {
            values = (String[])value;
        } else {
            values = new String[] {directory.getString(tag.getTagType())};
        }

        values = decodeValues(values);

        metadata.setValues(values);

        return metadata;
    }

    private String[] decodeValues(String[] values) {
        if (values == null) {
            return null;
        }

        String newValues[] = new String[values.length];

        for (int i = 0, valuesLength = values.length; i < valuesLength; i++) {
            String value = values[i];
            // TODO replace invalid characters
            newValues[i] = value;
        }

        return newValues;
    }

}
