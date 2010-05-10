package no.kantega.publishing.multimedia;

import no.kantega.publishing.common.data.Multimedia;

import java.io.IOException;

public interface ImageEditor {
    public Multimedia resizeMultimedia(Multimedia multimedia, int targetWidth, int targetHeight) throws IOException;
    public Multimedia resizeAndCropMultimedia(Multimedia mm, int width, int height, int cropx, int cropy, int cropwidth, int cropheight) throws IOException;
}
