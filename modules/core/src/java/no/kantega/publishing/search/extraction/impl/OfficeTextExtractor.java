package no.kantega.publishing.search.extraction.impl;

import no.kantega.publishing.search.extraction.TextExtractor;
import org.apache.log4j.Logger;
import org.apache.poi.POITextExtractor;
import org.apache.poi.extractor.ExtractorFactory;

import java.io.InputStream;

public class OfficeTextExtractor implements TextExtractor {
    private Logger log = Logger.getLogger(getClass());


    public String extractText(InputStream is) {
        try {
            POITextExtractor extractor = ExtractorFactory.createExtractor(is);
            return extractor.getText();
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            return "";
        }
    }
}
