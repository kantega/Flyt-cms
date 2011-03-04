package no.kantega.publishing.multimedia.metadata;

import no.kantega.publishing.common.data.Multimedia;
import no.kantega.publishing.multimedia.metadata.exif.DrewNoakesExifMetadataExtractor;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class JpegImageMetadataExtractorTest {
    private static final int BUFFER_SIZE = 2048;

    private JpegImageMetadataExtractor metadataExtractor;
    private Multimedia multimediaWithoutMetadata;
    private Multimedia multimediaWithoutMetadata2;


    @Before
    public void setUp() throws Exception {
        multimediaWithoutMetadata = getMultimediaForTest("testimage.jpg");
        multimediaWithoutMetadata2 = getMultimediaForTest("testimage2.jpg");

        metadataExtractor = new JpegImageMetadataExtractor();

        DrewNoakesExifMetadataExtractor exifExtractor = new DrewNoakesExifMetadataExtractor();
        metadataExtractor.setExifMetadataExtractor(exifExtractor);

    }

    private Multimedia getMultimediaForTest(String filename) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();


        byte[] buffer = new byte[BUFFER_SIZE];
        int numberOfBytesRead;
        while ((numberOfBytesRead = inputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
            outputStream.write(buffer, 0, numberOfBytesRead);
        }

        byte[] imageData = outputStream.toByteArray();

        Multimedia multimedia = new Multimedia();
        multimedia.setFilename(filename);
        multimedia.setData(imageData);

        return multimedia;
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

    @Test
    public void shouldGetAllExifData() {
        Multimedia multimediaWithMetadata = metadataExtractor.extractMetadata(multimediaWithoutMetadata2);
        assertEquals(66, multimediaWithMetadata.getExifMetadata().size());
    }

    @Test
    public void shouldExtractIptcKeywords() {
        Multimedia multimediaWithMetadata = metadataExtractor.extractMetadata(multimediaWithoutMetadata2);
        assertEquals("OpenAksessKeyword1, OpenAksesKeyword2",multimediaWithMetadata.getDescription());
    }

    @Test
    public void shouldExtractIptcCopyright() {
        Multimedia multimediaWithMetadata = metadataExtractor.extractMetadata(multimediaWithoutMetadata2);
        assertEquals("OpenAksessTest", multimediaWithMetadata.getAuthor());
    }

}
