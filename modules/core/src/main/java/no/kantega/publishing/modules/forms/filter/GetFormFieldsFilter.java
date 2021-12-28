package no.kantega.publishing.modules.forms.filter;

import no.kantega.commons.xmlfilter.Filter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class GetFormFieldsFilter implements Filter {
    private final List<String> tags = asList("input", "select", "textarea");
    private List<String> fieldNames =  new ArrayList<>();

    public List<String> getFieldNames() {
        return fieldNames;
    }

    @Override
    public Document runFilter(Document document) {
        for (String tag : tags) {
            for (Element element : document.getElementsByTag(tag)) {
                fieldNames.add(element.attr("name"));
            }
        }

        return document;
    }
}
