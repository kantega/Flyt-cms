package no.kantega.publishing.multimedia;

import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.MultimediaDimensions;

import java.io.IOException;

public interface ImageEditor {
    public Multimedia resizeMultimedia(Multimedia multimedia, int targetWidth, int targetHeight) throws IOException;
    public Multimedia resizeAndCropMultimedia(Multimedia mm, int width, int height, int cropx, int cropy, int cropwidth, int cropheight) throws IOException;
    public String getImageFormat();
    public MultimediaDimensions getResizedImageDimensions(int originalWidth, int originalHeight, int targetWidth, int targetHeight);
}
