package no.kantega.publishing.wro;

import no.kantega.publishing.wro.xmlmerge.XmlMerger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.servlet.ServletContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
public class OAWroManagerFactoryTest {

    @Test
    public void xmlFilesShouldBeMerged() throws TransformerException, IOException, SAXException, ParserConfigurationException, JDOMException {
        ServletContext mockServletContext = mock(ServletContext.class);
        // When
        String oaxml = "oa.xml";
        String projectXml = "project.xml";
        when(mockServletContext.getResourceAsStream(oaxml)).thenReturn(getClass().getResource(oaxml).openStream());
        when(mockServletContext.getResourceAsStream(projectXml)).thenReturn(getClass().getResource(projectXml).openStream());

        InputStream stream = XmlMerger.merge(oaxml, projectXml, mockServletContext);
        Document doc = new SAXBuilder().build(stream);

        // Then

        assertEquals(2, doc.getRootElement().getChildren().size());



        // And

        Element oa = (Element) doc.getRootElement().getChildren().get(0);
        Element project = (Element) doc.getRootElement().getChildren().get(1);

        assertEquals("admin-loginlayout", oa.getAttributeValue("name"));
        assertEquals("project-layout", project.getAttributeValue("name"));

    }
}
