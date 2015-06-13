package no.kantega.publishing.api.content;

import no.kantega.publishing.common.data.ContentTemplate;

import java.util.List;

public interface ContentTemplateAO {
    ContentTemplate getTemplateById(int id);
    ContentTemplate getTemplateById(int id, boolean updateFromFile);
    ContentTemplate getTemplateByPublicId(String id);
    List<ContentTemplate> getTemplates();
}
