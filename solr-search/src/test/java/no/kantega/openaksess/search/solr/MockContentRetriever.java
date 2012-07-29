package no.kantega.openaksess.search.solr;

import no.kantega.publishing.common.data.Content;
import no.kantega.search.api.retrieve.DocumentRetriever;
import org.springframework.stereotype.Component;

@Component
public class MockContentRetriever implements DocumentRetriever<Content>{
    public String getSupportedContentType() {
        return "aksess-document";
    }

    public Content getObjectById(int id) {
        Content content = new Content();
        content.setId(id);
        return content;

    }
}
