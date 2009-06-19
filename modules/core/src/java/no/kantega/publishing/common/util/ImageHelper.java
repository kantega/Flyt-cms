/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.common.util;

import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.common.Aksess;

import javax.imageio.ImageWriter;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.IIOImage;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.awt.*;
import java.awt.image.*;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

public class ImageHelper {
    public static Multimedia resizeImage(Multimedia mm, int width, int height) throws InterruptedException, IOException {
        return resizeAndCropImage(mm, width, height, -1, -1, -1, -1);
    }

    public static Multimedia resizeAndCropImage(Multimedia mm, int width, int height, int cropx, int cropy, int cropwidth, int cropheight) throws InterruptedException, IOException {
        // Krymp bildet og beskjær
        Image image = Toolkit.getDefaultToolkit().createImage(mm.getData());
        ImageHelper.ResizedImage img = resizeImage(image, width, height);

        BufferedImage newImage;

        if (cropx != -1 && cropy != -1 && cropwidth != -1 && cropheight != -1) {
            // Beskjær bilde
            BufferedImage cropImage = new BufferedImage(cropwidth, cropheight, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = cropImage.createGraphics();
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics2D.drawImage(img.getBufferedImage(), -cropx, -cropy, img.getWidth(), img.getHeight(), null);

            mm.setWidth(cropwidth);
            mm.setHeight(cropheight);
            newImage = cropImage;
        } else {
            mm.setWidth(img.getWidth());
            mm.setHeight(img.getHeight());
            newImage = img.getBufferedImage();
        }

        String imageFormat = Aksess.getOutputImageFormat();

        if (newImage.getWidth() * newImage.getHeight() <= Aksess.getPngPixelLimit()) {
            imageFormat = "png";
        }

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
            float q = ((float)Aksess.getOutputImageQuality())/((float)100);
            iwparam.setCompressionQuality(q);
        }
        writer.write(null, new IIOImage(newImage, null, null), iwparam);

        ios.flush();
        writer.dispose();


        // Update filename and data
        String filename = mm.getFilename();
        if (filename.indexOf(".") != -1) {
            filename = filename.substring(0, filename.lastIndexOf("."));
        }
        filename += "." + imageFormat;
        mm.setFilename(filename);
        mm.setData(bout.toByteArray());
        return mm;
    }

    private static ResizedImage resizeImage(Image img, int targetWidth, int targetHeight) throws InterruptedException {
        boolean higherQuality = true;
        int type = BufferedImage.TYPE_INT_RGB;

        MediaTracker mediaTracker = new MediaTracker(new Container());
        mediaTracker.addImage(img, 0);
        mediaTracker.waitForID(0);

        BufferedImage ret = null;

        // get size of src-image
        int w = img.getWidth(null);
        int h = img.getHeight(null);

        // make sure target-size is valid
        if (targetWidth == -1 || targetWidth > w) {
            targetWidth = w;
        }

        if (targetHeight == -1 || targetHeight > h) {
            targetHeight = h;
        }

        // make sure target is smaller that src-image
        if (w < targetWidth || h < targetHeight) {
            if (w < targetWidth) {
                w = targetWidth;
            }

            if (h < targetHeight) {
                h = targetHeight;
            }
        }

        // hints for rendering
        Map map = new HashMap();
        map.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        map.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        map.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // keep AR
        double thumbRatio = (double) targetWidth / (double) targetHeight;
        double imageRatio = (double) w / (double) h;
        if (thumbRatio < imageRatio) {
            targetHeight = (int) (targetWidth / imageRatio);
        } else {
            targetWidth = (int) (targetHeight * imageRatio);
        }


        if (!higherQuality) {
            w = targetWidth;
            h = targetHeight;
        }

        // do the resize
        int iter = 0;
        do {
            if (higherQuality && w > targetWidth) {
                w >>= 1;
                if (w < targetWidth) {
                    w = targetWidth;
                }
            }

            if (higherQuality && h > targetHeight) {
                h >>= 1;
                if (h < targetHeight) {
                    h = targetHeight;
                }
            }

            BufferedImage tmp = new BufferedImage(w, h, type);

            if (iter == 0) {
                Graphics g = tmp.createGraphics();
                g.drawImage(img, 0, 0, w, h, null);
                g.dispose();
            } else {
                Graphics2D g2 = tmp.createGraphics();
                g2.setRenderingHints(map);
                g2.drawImage(ret, 0, 0, w, h, null);
                g2.dispose();
            }

            ret = tmp;
            iter++;
        } while (w != targetWidth || h != targetHeight);

        // return new image
        return new ResizedImage(ret, w, h);
    }

    private static class ResizedImage {
        private BufferedImage bufferedImage;
        private int width;
        private int height;

        public ResizedImage(BufferedImage bufferedImage, int width, int height) {
            this.bufferedImage = bufferedImage;
            this.width = width;
            this.height = height;
        }

        public BufferedImage getBufferedImage() {
            return bufferedImage;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }

}
