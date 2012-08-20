package no.kantega.publishing.wro;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import ro.isdc.wro.manager.WroManager;
import ro.isdc.wro.manager.factory.ConfigurableWroManagerFactory;
import ro.isdc.wro.model.factory.WroModelFactory;
import ro.isdc.wro.model.factory.XmlModelFactory;

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

public class OAWroManagerFactory extends ConfigurableWroManagerFactory {

    private static final String OA_XML_CONFIG_FILE = "/WEB-INF/wro-oa.xml";
    private static final String PROJECT_XML_CONFIG_FILE = "/WEB-INF/wro-project.xml";

    private WroManager manager;

    @Override
    protected void onAfterInitializeManager(WroManager manager) {
        this.manager = manager;
    }

    @Override
    protected WroModelFactory newModelFactory() {

        return new XmlModelFactory(){
            @Override
            protected InputStream getModelResourceAsStream() throws IOException {
                try {
                    InputStream oaResourceStream = manager.getUriLocatorFactory().locate(OA_XML_CONFIG_FILE);

                    if(oaResourceStream == null) {
                        throw new IllegalStateException("Could not find WRO config file at " + OA_XML_CONFIG_FILE);
                    }

                    InputStream projectResourceStream = tryToGetProjectResource();

                    boolean onlyOA = projectResourceStream == null;
                    if(onlyOA) {
                        return oaResourceStream;
                    } else {
                        return merge(oaResourceStream, projectResourceStream);
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

            /**
             * WRO creates the stream in a way that tries to open the file when locating it.
             * so if it does not exist an IOexception is thrown.
             * @return a stream or null.
             */
            private InputStream tryToGetProjectResource() {
                InputStream projectResourceStream;
                try {
                    projectResourceStream = manager.getUriLocatorFactory().locate(PROJECT_XML_CONFIG_FILE);
                } catch (IOException e) {
                    projectResourceStream = null;
                }
                return projectResourceStream;
            }
        };
    }

    public InputStream merge(InputStream oaResourceStream, InputStream projectResourceStream) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = dbf.newDocumentBuilder();

        Document oaDoc = builder.parse(oaResourceStream);

        Document projectDoc = builder.parse(projectResourceStream);

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
