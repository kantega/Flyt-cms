package no.kantega.publishing.jobs.xmlimport;


import org.w3c.dom.Document;

public interface XMLImportValidator {
    boolean isValidXML(Document xml);
}
