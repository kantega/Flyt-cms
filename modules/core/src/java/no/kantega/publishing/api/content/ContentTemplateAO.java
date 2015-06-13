package no.kantega.publishing.api.content;

import no.kantega.publishing.common.data.ContentTemplate;

import java.util.List;

public interface ContentTemplateAO {
    ContentTemplate getTemplateById(int id);
    ContentTemplate getTemplateByPublicId(String id);
    List<ContentTemplate> getTemplates();
}
