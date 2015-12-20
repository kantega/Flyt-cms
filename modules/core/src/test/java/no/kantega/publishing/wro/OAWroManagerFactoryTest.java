package no.kantega.publishing.wro;

import no.kantega.publishing.wro.xmlmerge.XmlMerger;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.servlet.ServletContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
public class OAWroManagerFactoryTest {

    @Test
    public void xmlFilesShouldBeMerged() throws TransformerException, IOException, SAXException, ParserConfigurationException {
        ServletContext mockServletContext = mock(ServletContext.class);
        // When
        String oaxml = "oa.xml";
        String projectXml = "project.xml";
        when(mockServletContext.getResourceAsStream(oaxml)).thenReturn(getClass().getResource(oaxml).openStream());
        when(mockServletContext.getResourceAsStream(projectXml)).thenReturn(getClass().getResource(projectXml).openStream());

        InputStream stream = XmlMerger.merge(oaxml, projectXml, mockServletContext);
        String result = IOUtils.toString(stream);

        assertTrue(result.contains("admin-loginlayout"));
        assertTrue(result.contains("project-layout"));

    }
}
