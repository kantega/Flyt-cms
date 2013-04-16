package no.kantega.openaksess.search.provider.result;

import no.kantega.openaksess.search.provider.transformer.AttachmentTransformer;
import no.kantega.openaksess.search.provider.transformer.ContentTransformer;
import no.kantega.publishing.api.path.PathEntry;
import no.kantega.publishing.api.path.PathEntryService;
import no.kantega.search.api.provider.SearchResultDecorator;
import no.kantega.search.api.search.SearchQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class OASearchResultDecorator implements SearchResultDecorator<OASearchResult> {

    @Autowired
    private PathEntryService pathEntryService;

    @Override
    public Collection<String> handledindexedContentTypes() {
        return Arrays.asList(AttachmentTransformer.HANDLED_DOCUMENT_TYPE, ContentTransformer.HANDLED_DOCUMENT_TYPE);
    }

    @Override
    public OASearchResult decorate(Map<String, Object> resultMap, String title, String description, SearchQuery query) {
        Integer parentId = (Integer) resultMap.get("parentId");

        List<PathEntry> pathEntries = pathEntryService.getPathEntriesByAssociationIdInclusive(parentId);
        if (pathEntries.size() > 0) {
            pathEntries.remove(0);
        }
        return new OASearchResult((Integer) resultMap.get("id"),
                (Integer) resultMap.get("securityId"),
                (String) resultMap.get("indexedContentType"),
                title,
                description,
                (String) resultMap.get("author"),
                (String) resultMap.get("url"),
                parentId, pathEntries);
    }
}
