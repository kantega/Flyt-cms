package no.kantega.openaksess.search.taglib.label.resolver;

import no.kantega.publishing.common.cache.DocumentTypeCache;
import no.kantega.publishing.common.data.DocumentType;
import org.springframework.stereotype.Component;

@Component
public class DocumentTypeIdResolver implements LabelResolver{
    public String handledPrefix() {
        return "documentTypeId";
    }

    public String resolveLabel(String key) {
        DocumentType documentTypeById = DocumentTypeCache.getDocumentTypeById(Integer.parseInt(key));
        return documentTypeById == null ? "" : documentTypeById.getName();
    }
}
