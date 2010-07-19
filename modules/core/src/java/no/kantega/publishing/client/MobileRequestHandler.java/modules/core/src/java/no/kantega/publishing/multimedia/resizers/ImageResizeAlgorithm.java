package no.kantega.publishing.multimedia.resizers;

import no.kantega.publishing.common.data.Multimedia;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface ImageResizeAlgorithm {
    public BufferedImage resizeImage(BufferedImage mm, int width, int height);
}
