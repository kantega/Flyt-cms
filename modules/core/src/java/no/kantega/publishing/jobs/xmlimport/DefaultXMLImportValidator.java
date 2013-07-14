package no.kantega.publishing.jobs.xmlimport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DefaultXMLImportValidator implements XMLImportValidator{
    private static final Logger log = LoggerFactory.getLogger(DefaultXMLImportValidator.class);
    public boolean isValidXML(Document xml) {
        Element rootElement = xml.getDocumentElement();
        if (rootElement.getNodeName().equalsIgnoreCase("html")) {
            log.error( "Document contains HTML, skipping import");
            return false;
        }
        return true;
    }
}
