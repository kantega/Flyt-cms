package no.kantega.publishing.wro;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ro.isdc.wro.manager.factory.ConfigurableWroManagerFactory;
import ro.isdc.wro.model.factory.WroModelFactory;
import ro.isdc.wro.model.factory.XmlModelFactory;

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
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Date: Aug 26, 2010
 * Time: 12:10:39 PM
 *
 * @author tarkil
 */
public class OAWroManagerFactory extends ConfigurableWroManagerFactory {

    private static final String OA_XML_CONFIG_FILE = "/WEB-INF/wro-oa.xml";
    private static final String PROJECT_XML_CONFIG_FILE = "/WEB-INF/wro-project.xml";


    @Override
    protected WroModelFactory newModelFactory(final ServletContext servletContext) {
        return new XmlModelFactory() {
            @Override
            protected InputStream getConfigResourceAsStream() {
                try {
                    URL oaResource = servletContext.getResource(OA_XML_CONFIG_FILE);
                    URL projectResource = servletContext.getResource(PROJECT_XML_CONFIG_FILE);

                    if(oaResource == null) {
                        throw new IllegalStateException("Could not find WRO config file at " + OA_XML_CONFIG_FILE);
                    }

                    // Only OA config
                    if(projectResource == null) {
                        return oaResource.openStream();
                    } else {
                        return merge(oaResource, projectResource);
                    }
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                } catch (ParserConfigurationException e) {
                    throw new RuntimeException(e);
                } catch (SAXException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (TransformerException e) {
                    throw new RuntimeException(e);
                }

            }
        };
    }

    public InputStream merge(URL oaResource, URL projectResource) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        // We need to merge
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = dbf.newDocumentBuilder();

        Document oaDoc = builder.parse(oaResource.openStream(), oaResource.toExternalForm());

        Document projectDoc = builder.parse(projectResource.openStream(), projectResource.toExternalForm());


        return serialize(merge(oaDoc, projectDoc));
    }

    private InputStream serialize(Document merge) throws TransformerException, UnsupportedEncodingException {
        Transformer fac = TransformerFactory.newInstance().newTransformer();

        StringWriter writer = new StringWriter();
        fac.transform(new DOMSource(merge), new StreamResult(writer));
        return new ByteArrayInputStream(writer.toString().getBytes("utf-8"));
    }

    private Document merge(Document oaDoc, Document projectDoc) {


        NodeList children = projectDoc.getDocumentElement().getChildNodes();

        for(int i = 0; i < children.getLength(); i++) {
            Node importedNode = oaDoc.importNode(children.item(i), true);
            oaDoc.getDocumentElement().appendChild(importedNode);
        }

        return oaDoc;
    }

}
