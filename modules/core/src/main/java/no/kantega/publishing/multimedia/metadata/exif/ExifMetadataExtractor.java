package no.kantega.publishing.multimedia.metadata.exif;

import no.kantega.publishing.common.data.ExifMetadata;
import no.kantega.publishing.common.data.Multimedia;

import java.util.List;

public interface ExifMetadataExtractor {
    List<ExifMetadata> getMetadataForImage(Multimedia imageData);
}
