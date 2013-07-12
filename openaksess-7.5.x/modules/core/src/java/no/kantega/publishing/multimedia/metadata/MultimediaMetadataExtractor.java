package no.kantega.publishing.multimedia.metadata;

import no.kantega.publishing.common.data.Multimedia;

public interface MultimediaMetadataExtractor {
    boolean supportsMimeType(String mimeType);
    Multimedia extractMetadata(Multimedia multimedia);
}
