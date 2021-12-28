package no.kantega.commons.xmlfilter;

import org.jsoup.nodes.Document;

public interface Filter {
    Document runFilter(Document document);
}
