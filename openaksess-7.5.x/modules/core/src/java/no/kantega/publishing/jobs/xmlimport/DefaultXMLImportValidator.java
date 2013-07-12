package no.kantega.publishing.jobs.xmlimport;

import no.kantega.commons.log.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DefaultXMLImportValidator implements XMLImportValidator{
    public boolean isValidXML(Document xml) {
        Element rootElement = xml.getDocumentElement();
        if (rootElement.getNodeName().equalsIgnoreCase("html")) {
            Log.error(getClass().getName(), "Document contains HTML, skipping import");
            return false;
        }
        return true;
    }
}
