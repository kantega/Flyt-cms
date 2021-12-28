package no.kantega.publishing.common.ao;

import no.kantega.publishing.api.content.ContentTemplateAO;
import no.kantega.publishing.common.cache.ContentTemplateCache;
import no.kantega.publishing.common.data.ContentTemplate;

import java.util.List;

public class ContentTemplateAOImpl implements ContentTemplateAO {
    @Override
    public ContentTemplate getTemplateById(int id) {
        return ContentTemplateCache.getTemplateById(id);
    }

    @Override
    public ContentTemplate getTemplateById(int id, boolean updateFromFile) {
        return ContentTemplateCache.getTemplateById(id, updateFromFile);
    }

    @Override
    public ContentTemplate getTemplateByPublicId(String id) {
        return ContentTemplateCache.getTemplateByPublicId(id);
    }

    @Override
    public List<ContentTemplate> getTemplates() {
        return ContentTemplateCache.getTemplates();
    }
}
