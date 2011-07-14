package no.kantega.publishing.wro;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class OAWroManagerFactoryTest {

    @Test
    public void xmlFilesShouldBeMerged() throws TransformerException, IOException, SAXException, ParserConfigurationException, JDOMException {

        // Given

        OAWroManagerFactory fac = new OAWroManagerFactory();


        // When
        InputStream stream = fac.merge(getClass().getResource("oa.xml"), getClass().getResource("project.xml"));
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
