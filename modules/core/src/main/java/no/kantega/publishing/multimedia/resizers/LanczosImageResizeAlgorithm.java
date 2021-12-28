package no.kantega.publishing.multimedia.resizers;

import com.mortennobel.imagescaling.ResampleOp;

import java.awt.image.BufferedImage;

public class LanczosImageResizeAlgorithm implements ImageResizeAlgorithm {
    public BufferedImage resizeImage(BufferedImage image, int width, int height) {
        ResampleOp resampleOp = new ResampleOp (width, height);
        return resampleOp.filter(image, null);
    }
}
