package no.kantega.publishing.multimedia.metadata.exif;

import java.util.List;

public interface ExifMetadataExtractor {
    public List<ExifMetadata> getMetadataForImage(byte[] imageData);
}
