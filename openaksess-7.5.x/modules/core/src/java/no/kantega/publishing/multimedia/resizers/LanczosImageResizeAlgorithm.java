package no.kantega.publishing.multimedia.resizers;

import com.mortennobel.imagescaling.ResampleOp;

import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: terros
 * Date: Mar 8, 2010
 * Time: 3:08:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class LanczosImageResizeAlgorithm implements ImageResizeAlgorithm {
    public BufferedImage resizeImage(BufferedImage image, int width, int height) {
        ResampleOp resampleOp = new ResampleOp (width, height);
        return resampleOp.filter(image, null);
    }
}
