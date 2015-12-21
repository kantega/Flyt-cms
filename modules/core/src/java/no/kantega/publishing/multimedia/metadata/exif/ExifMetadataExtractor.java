package no.kantega.publishing.multimedia.metadata.exif;

import no.kantega.publishing.common.data.ExifMetadata;

import java.util.List;

public interface ExifMetadataExtractor {
    List<ExifMetadata> getMetadataForImage(byte[] imageData);
}
