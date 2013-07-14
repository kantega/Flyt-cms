package no.kantega.publishing.multimedia;

import no.kantega.publishing.common.data.ImageResizeParameters;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.MultimediaDimensions;
import no.kantega.publishing.common.data.enums.Cropping;
import no.kantega.publishing.common.data.enums.MultimediaType;
import no.kantega.publishing.common.exception.InvalidImageFormatException;
import no.kantega.publishing.multimedia.resizers.ImageResizeAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;


public class DefaultImageEditor implements ImageEditor {
    private static final Logger log = LoggerFactory.getLogger(DefaultImageEditor.class);
    ImageResizeAlgorithm imageResizeAlgorithm;

    private String defaultImageFormat = "png";

    private int jpgOutputQuality = 85;

    public Multimedia resizeMultimedia(Multimedia multimedia, int targetWidth, int targetHeight) throws IOException, InvalidImageFormatException {
        return resizeAndCropMultimedia(multimedia, targetWidth, targetHeight, -1, -1, -1, -1);
    }

    /**
     * {@inheritDoc}
     */
    public Multimedia resizeAndCropMultimedia(Multimedia multimedia, int targetWidth, int targetHeight, int cropX, int cropY, int cropWidth, int cropHeight) throws IOException, InvalidImageFormatException {

        BufferedImage image = getImageFromMultimedia(multimedia);
        String imageFormat = getDefaultImageFormat();
        if (multimedia.getMimeType().getType().contains("jpeg")) {
            imageFormat = "jpg";
        }

        image = resizeImage(image, targetWidth, targetHeight, Cropping.CONTAIN);
        if (cropX != -1 && cropY != -1 && cropWidth != -1 && cropHeight != -1) {
            image = cropImage(image, cropX, cropY, cropWidth, cropHeight);
        }
        Multimedia mm = updateMultimedia(image, multimedia);
        return mm;
    }

    /**
     * Crop Image
     * @param image
     * @param cropX
     * @param cropY
     * @param cropWidth
     * @param cropHeight
     * @return
     */
    public BufferedImage cropImage(BufferedImage image, int cropX, int cropY, int cropWidth, int cropHeight){
        if (cropWidth > 0 && cropHeight > 0){
            BufferedImage cropImage = new BufferedImage(cropWidth, cropHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = cropImage.createGraphics();
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics2D.drawImage(image, -cropX, -cropY, image.getWidth(), image.getHeight(), null);
            return cropImage;
        }
        return image;
    }

    /**
     * Updates a multimedia object with supplied Image
     * @param image
     * @param multimedia
     * @return
     * @throws IOException
     */
    private Multimedia updateMultimedia(BufferedImage image, Multimedia multimedia) throws IOException {
        // Determine output format
        String imageFormat = getDefaultImageFormat();
        if (multimedia.getMimeType().getType().contains("jpeg")) {
            imageFormat = "jpg";
        }

        // Write image
        ImageWriter writer = null;
        Iterator iter = ImageIO.getImageWritersByFormatName(imageFormat);
        if (iter.hasNext()) {
            writer = (ImageWriter)iter.next();
        }

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ImageOutputStream ios = ImageIO.createImageOutputStream(bout);
        writer.setOutput(ios);

        ImageWriteParam iwparam = null;
        if (imageFormat.equalsIgnoreCase("jpg")) {
            iwparam = new JPEGImageWriteParam(Locale.getDefault());
            iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            float q = ((float) jpgOutputQuality)/((float)100);
            iwparam.setCompressionQuality(q);
        }
        writer.write(null, new IIOImage(image, null, null), iwparam);

        ios.flush();
        writer.dispose();


        // Update filename and data
        String filename = multimedia.getFilename();
        if (filename.contains(".")) {
            filename = filename.substring(0, filename.lastIndexOf("."));
        }
        filename += "." + imageFormat;
        multimedia.setWidth(image.getWidth());
        multimedia.setHeight(image.getHeight());
        multimedia.setFilename(filename);
        multimedia.setData(bout.toByteArray());
        return multimedia;

    }

    /**
     * Extracts BufferedImage from multimedia object
     * @param multimedia
     * @return BufferedImage representing the image in the multimedia object.
     * @throws InvalidImageFormatException if unable to read the image.
     */
    private BufferedImage getImageFromMultimedia(Multimedia multimedia) throws InvalidImageFormatException{
        if (multimedia.getType() == MultimediaType.MEDIA) {
            if (multimedia.getMimeType() != null && multimedia.getMimeType().getType().contains("image")) {
                BufferedImage image;

                try {
                    image = ImageIO.read(new ByteArrayInputStream(multimedia.getData()));
                } catch (IOException e) {
                    log.error( "Failed converting image, probably CMYK, install Java Advanced Imaging API on server");
                    throw new InvalidImageFormatException("Failed converting image", e);
                }
                return image;
            }
        }
        return null;
    }


    /**
     * Resize Image with supplied cropping method
     * @param image
     * @param targetWidth
     * @param targetHeight
     * @param cropping
     * @return
     * @throws InvalidImageFormatException
     */
    public BufferedImage resizeImage(BufferedImage image, int targetWidth, int targetHeight, Cropping cropping) throws InvalidImageFormatException {
        // Get resized image dimensions with correct aspect ratio
        MultimediaDimensions d = getResizedImageDimensions(image.getWidth(), image.getHeight(), targetWidth, targetHeight, cropping);
        targetWidth = d.getWidth();
        targetHeight = d.getHeight();

        return imageResizeAlgorithm.resizeImage(image, targetWidth, targetHeight);
    }

    /**
     * Get dimensions of resized image with correct aspect ratio
     * @param originalWidth - original width of image
     * @param originalHeight  - original height of image
     * @param targetWidth - width of resized image
     * @param targetHeight - height of resized image
     * @param cropping - optional cropping method.
     * @return - MultimediaDimensions - dimensions of resized image
     */
    public MultimediaDimensions getResizedImageDimensions(int originalWidth, int originalHeight, int targetWidth, int targetHeight, Cropping cropping) {

        if (targetHeight == -1) targetHeight = originalHeight;
        if (targetWidth == -1) targetWidth = originalWidth;

        double srcRatio = (double) originalWidth / (double) originalHeight;
        double trgRatio = (double) targetWidth / (double) targetHeight;

        double newWidth = -1;
        double newHeight = -1;

        if (cropping == Cropping.CONTAIN){

            if (srcRatio > trgRatio){
                // preserve Width
                newWidth    = targetWidth;
                newHeight   = newWidth / srcRatio;

            } else{
                // preserve Height
                newHeight   = targetHeight;
                newWidth    = newHeight * srcRatio;
            }
        } else {
            if (srcRatio < trgRatio){
                // preserve Width
                newWidth    = targetWidth;
                newHeight   = newWidth / srcRatio;

            } else{
                // preserve Height
                newHeight   = targetHeight;
                newWidth    = newHeight * srcRatio;
            }
        }

        return new MultimediaDimensions( (int) Math.ceil(newWidth), (int) Math.ceil(newHeight));
    }

    /**
     * Get dimensions of resized image with correct aspect ratio
     * @param originalWidth - original width of image
     * @param originalHeight  - original height of image
     * @param targetWidth - max width of resized image
     * @param targetHeight - max height of resized image
     * @return - MultimediaDimensions - dimensions of resized image
     */
    public MultimediaDimensions getResizedImageDimensions(int originalWidth, int originalHeight, int targetWidth, int targetHeight) {
        return getResizedImageDimensions(originalWidth, originalHeight, targetWidth, targetHeight, Cropping.CONTAIN);
    }

    /**
     * Resize a multimedia object, and prepare it for cropping
     * use Cropping.CONTAIN for standard resize without cropping.
     * If cropping operations are to be performed on the multimediaobject afterwards, targetWidth and targetHeight should be the same as the desired width and height
     * of the final resised and cropped media object.
     * For best results, the original media object should be larger than the resized object in both width and height.
     * Cropping.CONTAIN is standard, and will resize the object to ensure that the whole image fits inside the box specified by targetWidth and targetHeight
     * Cropping.TOPLEFT and Cropping.CENTERED will resize the object down, but ensure the image completely covers the box specified by targetWidth and targetHeight
     * @param multimedia - multimedia object
     * @param resizeParameters - ImageResizeParameters
     * @return resized multimedia object
     * @throws IOException
     * @throws InvalidImageFormatException
     */
    public Multimedia resizeMultimedia(Multimedia multimedia, ImageResizeParameters resizeParameters) throws IOException, InvalidImageFormatException {
        BufferedImage image = getImageFromMultimedia(multimedia);

        int targetWidth = resizeParameters.getMaxWidth();
        int targetHeight = resizeParameters.getMaxHeight();

        if ((targetWidth < 3 && targetHeight < 3)) {
            log.error( "Minimum resize dimensions are 3x3. Values below threshold will be adjusted");
            if (targetWidth < 3 && targetWidth != -1) targetWidth = 3;
            if (targetHeight < 3 && targetHeight != -1) targetHeight = 3;
        }

        Cropping cropping = resizeParameters.getCropping();
        if (targetWidth == -1 || targetHeight == -1) {
            cropping = Cropping.CONTAIN;
        }

        if (targetHeight < image.getHeight() && targetHeight != -1 || targetWidth < image.getWidth() && targetWidth != -1) {
            BufferedImage resizedImage = resizeImage(image, targetWidth, targetHeight, cropping);
            BufferedImage croppedImage = cropImage(resizedImage, targetWidth, targetHeight, cropping);
            updateMultimedia(croppedImage, multimedia);
        }
        return multimedia;
    }

    /**
     * Crop multimedia object, using desired cropping method.
     * @param image - BufferedImage
     * @param cropWidth - width of bounding box
     * @param cropHeight - height of bounding box
     * @param cropping - cropping method. TOPLEFT crops from top left corner. CENTERED crops from media object center. CONTAIN is the same as TOPLEFT if used isolated,
     * and is superflous if media object is alredy resized using CONTAIN method.
     * @return
     * @throws IOException
     * @throws InvalidImageFormatException
     */
    private BufferedImage cropImage(BufferedImage image, int cropWidth, int cropHeight, Cropping cropping) throws IOException, InvalidImageFormatException {
        BufferedImage croppedImage = image;

        switch (cropping){
            case CONTAIN:{
                if (cropWidth < image.getWidth() || cropHeight < image.getHeight()){
                    croppedImage = cropImage(image, 0, 0, cropWidth, cropHeight);
                }
                break;
            }
            case TOPLEFT:
                croppedImage = cropImage(image, 0, 0, cropWidth, cropHeight);
                break;
            case CENTERED:
                croppedImage = cropImage(image, (image.getWidth() - cropWidth) / 2, (image.getHeight() - cropHeight) / 2, cropWidth, cropHeight);
                break;
            default:
                if (cropWidth < image.getWidth() || cropHeight < image.getHeight()){
                    croppedImage = cropImage(image, 0, 0, cropWidth, cropHeight);
                }
                break;
        }
        return croppedImage;
    }

    public void setImageResizeAlgorithm(ImageResizeAlgorithm imageResizeAlgorithm) {
        this.imageResizeAlgorithm = imageResizeAlgorithm;
    }

    public String getDefaultImageFormat() {
        return defaultImageFormat;
    }

    public void setDefaultImageFormat(String defaultImageFormat) {
        this.defaultImageFormat = defaultImageFormat;
    }

    public void setJpgOutputQuality(int jpgOutputQuality) {
        this.jpgOutputQuality = jpgOutputQuality;
    }
}
