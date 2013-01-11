package no.kantega.openaksess.search.solr;

import no.kantega.publishing.common.data.Content;
import no.kantega.search.api.retrieve.DocumentRetriever;
import org.springframework.stereotype.Component;

@Component
public class DerpDocumentRetriever implements DocumentRetriever<Content> {
    public String getSupportedContentType() {
        return "derp-document";
    }

    public Content getObjectById(int id) {
        return null;
    }
}
