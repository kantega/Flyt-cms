package no.kantega.publishing.multimedia;

import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.MultimediaDimensions;
import no.kantega.publishing.common.exception.InvalidImageFormatException;

import java.io.IOException;

public interface ImageEditor {
    public Multimedia resizeMultimedia(Multimedia multimedia, int targetWidth, int targetHeight) throws IOException, InvalidImageFormatException;
    public Multimedia resizeAndCropMultimedia(Multimedia mm, int width, int height, int cropx, int cropy, int cropwidth, int cropheight) throws IOException, InvalidImageFormatException;
    public MultimediaDimensions getResizedImageDimensions(int originalWidth, int originalHeight, int targetWidth, int targetHeight);
}
