package no.kantega.publishing.common.data.attributes;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for ListAttribute
 *
 * @author Håvard Wigtil (Kantega AS)
 */
public class ListAttributeTest {

    private ListAttribute listAttribute;


    @Before
    public void setUp() throws Exception {
        listAttribute = new ListAttribute();
    }

    /**
     * Basic testing of setConfig() to cover XPath refactoring.
     *
     * @throws Exception
     */
    @Test
    public void testSetConfig() throws Exception {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse(getClass().getResourceAsStream("/templates/nyheter.xml"));
        XPath xpath = XPathFactory.newInstance().newXPath();
        Element config = (Element)xpath.evaluate("/template/attributes/attribute[@name='vis arkiverte']", doc, XPathConstants.NODE);

        listAttribute.setConfig(config, null);

        assertEquals(2, listAttribute.getListOptions().size());
    }
}
