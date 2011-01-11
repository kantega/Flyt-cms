package no.kantega.publishing.common.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PrettyURLEncoderTest {
    @Test
    public void testEncode() throws Exception {
        String title = "The/quick&brown.fox jumps over the \"lazy\" dog";
        assertEquals("Encoding failed:", "The-quickbrown.fox-jumps-over-the-lazy-dog", PrettyURLEncoder.encode(title));
    }
}
