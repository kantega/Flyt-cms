package no.kantega.publishing.multimedia;

import no.kantega.publishing.common.data.ImageResizeParameters;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.MultimediaDimensions;
import no.kantega.publishing.common.data.enums.Cropping;
import no.kantega.publishing.common.exception.InvalidImageFormatException;
import org.apache.xpath.operations.Mult;

import java.io.IOException;

public interface ImageEditor {
    public Multimedia resizeMultimedia(Multimedia multimedia, int targetWidth, int targetHeight) throws IOException, InvalidImageFormatException;

     /**
     * Legcy method used in crop and resize batch operations from the admin GUI
     * @param multimedia
     * @param targetWidth
     * @param targetHeight
     * @param cropX
     * @param cropY
     * @param cropWidth
     * @param cropHeight
     * @return
     * @throws IOException
     * @throws InvalidImageFormatException
     */
    @Deprecated
    public Multimedia resizeAndCropMultimedia(Multimedia multimedia, int targetWidth, int targetHeight, int cropX, int cropY, int cropWidth, int cropHeight) throws IOException, InvalidImageFormatException;

    public MultimediaDimensions getResizedImageDimensions(int originalWidth, int originalHeight, int targetWidth, int targetHeight);

    public Multimedia resizeMultimedia(Multimedia mm, ImageResizeParameters resizeParameters)  throws IOException, InvalidImageFormatException;

    /**
     * Rotates a multimedia object cointaining an image.
     * @param multimedia
     * @param degrees
     * @return
     * @throws InvalidImageFormatException
     * @throws IOException
     */
    public Multimedia rotateMultimedia(Multimedia mm, int degrees) throws IOException, InvalidImageFormatException;
}
