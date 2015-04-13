package no.kantega.publishing.multimedia.resizers;

import java.awt.image.BufferedImage;

public interface ImageResizeAlgorithm {
    public BufferedImage resizeImage(BufferedImage mm, int width, int height);
}
