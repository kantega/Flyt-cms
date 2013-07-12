package no.kantega.publishing.jobs.xmlimport;

import no.kantega.commons.util.XMLHelper;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DefaultXMLImportValidatorTest {

    @Test
    public void shouldFailForHTML() {
        String html = "<html><body>test</body></html>";

        DefaultXMLImportValidator validator = new DefaultXMLImportValidator();

        assertFalse("isValidXML(html)", validator.isValidXML(XMLHelper.getDocument(html)));
    }

    @Test
    public void shouldValidateRss() {
        String rss = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?><rss><channel></channel></rss>";

        DefaultXMLImportValidator validator = new DefaultXMLImportValidator();

        assertTrue("isValidXML(rss)", validator.isValidXML(XMLHelper.getDocument(rss)));
    }
}
