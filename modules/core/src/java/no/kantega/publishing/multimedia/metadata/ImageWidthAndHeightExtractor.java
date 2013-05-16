package no.kantega.publishing.multimedia.metadata;

import no.kantega.commons.media.ImageInfo;
import no.kantega.commons.media.MimeType;
import no.kantega.commons.media.MimeTypes;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Multimedia;

import java.io.ByteArrayInputStream;

public class ImageWidthAndHeightExtractor implements MultimediaMetadataExtractor{
    public boolean supportsMimeType(String mimeType) {
        return true;
    }

    public Multimedia extractMetadata(Multimedia multimedia) {
        MimeType mimeType = MimeTypes.getMimeType(multimedia.getFilename());

        if (mimeType.getType().contains("image") || mimeType.getType().contains("flash")) {
            // For images and Flash we can find the dimensions
            ImageInfo ii = new ImageInfo();
            ii.setInput(new ByteArrayInputStream(multimedia.getData()));
            if (ii.check()) {
                multimedia.setWidth(ii.getWidth());
                multimedia.setHeight(ii.getHeight());
            }
        } else if (mimeType.isDimensionRequired() && (multimedia.getWidth() <= 0 || multimedia.getHeight() <= 0)) {
            multimedia.setWidth(Aksess.getDefaultMediaWidth());
            multimedia.setHeight(Aksess.getDefaultMediaHeight());
        }

        return multimedia;
    }
}
