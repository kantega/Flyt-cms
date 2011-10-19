package no.kantega.publishing.multimedia;

import no.kantega.commons.log.Log;
import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.data.MultimediaDimensions;
import no.kantega.publishing.common.data.enums.Cropping;
import no.kantega.publishing.common.data.enums.MultimediaType;
import no.kantega.publishing.common.exception.InvalidImageFormatException;
import no.kantega.publishing.multimedia.resizers.ImageResizeAlgorithm;
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
    ImageResizeAlgorithm imageResizeAlgorithm;

    private String defaultImageFormat = "png";

    private int jpgOutputQuality = 85;

    public Multimedia resizeMultimedia(Multimedia multimedia, int targetWidth, int targetHeight) throws IOException, InvalidImageFormatException {
        return resizeAndCropMultimedia(multimedia, targetWidth, targetHeight, -1, -1, -1, -1);
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
     * @param targetWidth - desired width
     * @param targetHeight - desired height
     * @param cropping - desired cropping method to prepare for.
     * @return resized multimedia object
     * @throws IOException
     * @throws InvalidImageFormatException
     */
    public Multimedia resizeMultimedia(Multimedia multimedia, int targetWidth, int targetHeight, Cropping cropping) throws IOException, InvalidImageFormatException {
        BufferedImage image = getImageFromMultimedia(multimedia);
        BufferedImage img = resizeImage(image, targetWidth, targetHeight, cropping);
        multimedia = updateMultimedia(img, multimedia);
        return multimedia;
    }

    /**
     * Crop multimedia object, using desired cropping method.
     * @param mm - Multimedia object
     * @param cropWidth - width of bounding box
     * @param cropHeight - height of bounding box
     * @param cropping - cropping method. TOPLEFT crops from top left corner. CENTERED crops from media object center. CONTAIN is the same as TOPLEFT if used isolated,
     * and is superflous if media object is alredy resized using CONTAIN method.
     * @return
     * @throws IOException
     * @throws InvalidImageFormatException
     */
    public Multimedia cropMultimedia(Multimedia mm, int cropWidth, int cropHeight, Cropping cropping) throws IOException, InvalidImageFormatException {

        switch (cropping){
            case CONTAIN:{
                if (cropWidth < mm.getWidth() || cropHeight < mm.getHeight()){
                    mm = cropMultimedia(mm, 0, 0, cropWidth, cropHeight);
                }
                break;
            }
            case TOPLEFT:
                mm = cropMultimedia(mm, 0, 0, cropWidth, cropHeight); break;
            case CENTERED:
                mm = cropMultimedia(mm, (mm.getWidth() - cropWidth) / 2, (mm.getHeight() - cropHeight) / 2, cropWidth, cropHeight); break;
            default:
                if (cropWidth < mm.getWidth() || cropHeight < mm.getHeight()){
                    mm = cropMultimedia(mm, 0, 0, cropWidth, cropHeight);
                }
                break;
        }
        return mm;
    }

    /**
     * Crop a multimedia object.
     *
     * @param multimedia
     * @param cropX
     * @param cropY
     * @param cropW
     * @param cropH
     * @return
     * @throws InvalidImageFormatException
     * @throws IOException
     */
    public Multimedia cropMultimedia(Multimedia multimedia, int cropX, int cropY, int cropW, int cropH) throws InvalidImageFormatException, IOException {
        BufferedImage image = getImageFromMultimedia(multimedia);
        image = cropImage(image, cropX, cropY, cropW, cropH);
        multimedia = updateMultimedia(image, multimedia);
        return multimedia;
    }

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


    public BufferedImage cropImage(BufferedImage image, int cropX, int cropY, int cropWidth, int cropHeight){
        // Crop image
        if (cropWidth > 0 && cropHeight > 0){
            BufferedImage cropImage = new BufferedImage(cropWidth, cropHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = cropImage.createGraphics();
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics2D.drawImage(image, -cropX, -cropY, image.getWidth(), image.getHeight(), null);
            return cropImage;
        }
        return image;
    }

    public Multimedia updateMultimedia(BufferedImage image, Multimedia mm) throws IOException {
        //Multimedia multimedia = new Multimedia();
        // Determine output format
        String imageFormat = getDefaultImageFormat();
        if (mm.getMimeType().getType().contains("jpeg")) {
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
        String filename = mm.getFilename();
        if (filename.indexOf(".") != -1) {
            filename = filename.substring(0, filename.lastIndexOf("."));
        }
        filename += "." + imageFormat;
        mm.setWidth(image.getWidth());
        mm.setHeight(image.getHeight());
        mm.setFilename(filename);
        mm.setData(bout.toByteArray());
        return mm;

    }

    public BufferedImage getImageFromMultimedia(Multimedia multimedia) throws InvalidImageFormatException{
        if (multimedia.getType() == MultimediaType.MEDIA) {
            if (multimedia.getMimeType() != null && multimedia.getMimeType().getType().contains("image")) {
                BufferedImage image;

                try {
                    image = ImageIO.read(new ByteArrayInputStream(multimedia.getData()));
                } catch (IOException e) {
                    Log.error(this.getClass().getName(), "Failed converting image, probably CMYK, install Java Advanced Imaging API on server");
                    // CMYK image
                    throw new InvalidImageFormatException(this.getClass().getName(), "", e);
                }
                return image;
            }
        }
        return null;
    }

    public BufferedImage resizeImage(BufferedImage image, int targetWidth, int targetHeight, Cropping cropping) throws InvalidImageFormatException {


        // Make sure target-size is valid
        int w = image.getWidth();
        int h = image.getHeight();



        // Get resized image dimensions with correct aspect ratio
        MultimediaDimensions d = getResizedImageDimensions(w, h, targetWidth, targetHeight, cropping);
        targetWidth = d.getWidth();
        targetHeight = d.getHeight();

        BufferedImage resizedImage = imageResizeAlgorithm.resizeImage(image, targetWidth, targetHeight);

        return resizedImage;
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
