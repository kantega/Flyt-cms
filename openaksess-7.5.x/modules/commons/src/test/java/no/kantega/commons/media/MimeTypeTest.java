package no.kantega.commons.media;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MimeTypeTest {

    @Test
    public void userMustInputDimensionsIfTypeContainsVideoButNotFlash(){
        assertTrue("userMustInputDimension should be true", MimeTypes.getMimeType("video.avi").userMustInputDimension());
        assertTrue("userMustInputDimension should be true", MimeTypes.getMimeType("video.wmv").userMustInputDimension());
        assertTrue("userMustInputDimension should be true", MimeTypes.getMimeType("video.mpeg").userMustInputDimension());
        assertTrue("userMustInputDimension should be true", MimeTypes.getMimeType("video.mp4").userMustInputDimension());

        assertFalse("userMustInputDimension should be false", MimeTypes.getMimeType("video.flv").userMustInputDimension());
    }

    @Test
    public void dimensionRequiredIfVideoOrImageOrFlash(){
        assertTrue("userMustInputDimension should be true", MimeTypes.getMimeType("video.bmp").isDimensionRequired());
        assertTrue("userMustInputDimension should be true", MimeTypes.getMimeType("video.jpg").isDimensionRequired());
        assertTrue("userMustInputDimension should be true", MimeTypes.getMimeType("video.jpeg").isDimensionRequired());
        assertTrue("userMustInputDimension should be true", MimeTypes.getMimeType("video.gif").isDimensionRequired());

        assertTrue("userMustInputDimension should be true", MimeTypes.getMimeType("video.avi").isDimensionRequired());
        assertTrue("userMustInputDimension should be true", MimeTypes.getMimeType("video.wmv").isDimensionRequired());
        assertTrue("userMustInputDimension should be true", MimeTypes.getMimeType("video.mpeg").isDimensionRequired());
        assertTrue("userMustInputDimension should be true", MimeTypes.getMimeType("video.mp4").isDimensionRequired());

        assertTrue("userMustInputDimension should be true", MimeTypes.getMimeType("video.swf").isDimensionRequired());
        assertTrue("userMustInputDimension should be true", MimeTypes.getMimeType("video.swt").isDimensionRequired());

        assertFalse("userMustInputDimension should be false", MimeTypes.getMimeType("video.flv").isDimensionRequired());
    }
}
