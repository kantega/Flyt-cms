package no.kantega.publishing.multimedia.metadata;

import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.multimedia.metadata.exif.DrewNoakesExifMetadataExtractor;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.junit.Assert.*;

public class JpegImageMetadataExtractorTest {
    private static final int BUFFER_SIZE = 2048;

    private JpegImageMetadataExtractor metadataExtractor;
    private Multimedia multimediaWithoutMetadata;


    @Before
    public void setUp() throws Exception {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("testimage.jpg");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();


        byte[] buffer = new byte[BUFFER_SIZE];
        int numberOfBytesRead;
        while ((numberOfBytesRead = inputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
            outputStream.write(buffer, 0, numberOfBytesRead);
        }

        byte[] imageData = outputStream.toByteArray();

        multimediaWithoutMetadata = new Multimedia();
        multimediaWithoutMetadata.setFilename("testimage.jpg");
        multimediaWithoutMetadata.setData(imageData);

        metadataExtractor = new JpegImageMetadataExtractor();

        DrewNoakesExifMetadataExtractor exifExtractor = new DrewNoakesExifMetadataExtractor();
        metadataExtractor.setExifMetadataExtractor(exifExtractor);

    }


    @Test
    public void shouldAcceptJpeg() {
        assertTrue(metadataExtractor.supportsMimeType("image/jpeg"));
    }

    @Test
    public void shouldNotAcceptGif() {
        assertFalse(metadataExtractor.supportsMimeType("image/gif"));
    }


    @Test
    public void shouldGetCameraModelAndMake() {
        Multimedia multimediaWithMetadata = metadataExtractor.extractMetadata(multimediaWithoutMetadata);
        assertEquals("Apple", multimediaWithMetadata.getCameraMake());
        assertEquals("iPhone", multimediaWithMetadata.getCameraModel());
    }

    @Test
    public void shouldGetOriginalDate() {
        Multimedia multimediaWithMetadata = metadataExtractor.extractMetadata(multimediaWithoutMetadata);
        assertNotNull(multimediaWithMetadata.getOriginalDate());
    }

    @Test
    public void shouldGetGPSPosition() {
        Multimedia multimediaWithMetadata = metadataExtractor.extractMetadata(multimediaWithoutMetadata);
        assertEquals("N", multimediaWithMetadata.getGpsLatitudeRef());
        assertEquals("E", multimediaWithMetadata.getGpsLongitudeRef());
    }
}
