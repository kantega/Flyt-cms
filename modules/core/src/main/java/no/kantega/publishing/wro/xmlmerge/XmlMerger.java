package no.kantega.publishing.wro.xmlmerge;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class XmlMerger {

    /**
     * @param oaXml xml from Flyt CMS
     * @param projectXml xml from the current project
     * @param servletContext -
     * @return inputstream containing projectXml merged into oaXml.
     */
    public static InputStream merge(String oaXml, String projectXml, ServletContext servletContext){
        try {
            InputStream oaResourceStream = servletContext.getResourceAsStream(oaXml);

            if(oaResourceStream == null) {
                throw new IllegalStateException("Could not find WRO config file at " + oaXml);
            }

            InputStream projectResourceStream = servletContext.getResourceAsStream(projectXml);

            boolean onlyOA = projectResourceStream == null;
            if(onlyOA) {
                return oaResourceStream;
            } else {
                return mergeStreams(oaResourceStream, projectResourceStream);
            }
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
            throw new RuntimeException(e);
        }
    }


    private static InputStream mergeStreams(InputStream oaResourceStream, InputStream projectResourceStream) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = dbf.newDocumentBuilder();

        Document oaDoc = builder.parse(oaResourceStream);

        Document projectDoc = builder.parse(projectResourceStream);

        return serialize(merge(oaDoc, projectDoc));
    }

    private static InputStream serialize(Document merge) throws TransformerException, UnsupportedEncodingException {
        Transformer fac = TransformerFactory.newInstance().newTransformer();

        StringWriter writer = new StringWriter();
        fac.transform(new DOMSource(merge), new StreamResult(writer));
        return new ByteArrayInputStream(writer.toString().getBytes("utf-8"));
    }

    private static Document merge(Document oaDoc, Document projectDoc) {
        NodeList children = projectDoc.getDocumentElement().getChildNodes();

        for(int i = 0; i < children.getLength(); i++) {
            Node importedNode = oaDoc.importNode(children.item(i), true);
            oaDoc.getDocumentElement().appendChild(importedNode);
        }

        return oaDoc;
    }

}
