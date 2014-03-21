package no.kantega.commons.util;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class XPathHelperTest {

    @Test
    public void shouldParseExpression() throws ParserConfigurationException {

        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element root = document.createElement("config");

        document.appendChild(root);

        Element helptext = document.createElement("helptext");
        helptext.appendChild(document.createTextNode("some text"));
        root.appendChild(helptext);


        assertEquals(XPathHelper.getString(root, "helptext"), "some text");

    }
}
