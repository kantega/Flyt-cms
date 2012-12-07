package no.kantega.publishing.common.ao;

import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.path.PathEntry;
import no.kantega.publishing.api.path.PathEntryAO;
import no.kantega.publishing.common.service.impl.PathWorker;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

import java.util.List;

public class PathEntryAOLegacyImpl extends NamedParameterJdbcDaoSupport implements PathEntryAO {
    @Override
    public List<PathEntry> getPathEntriesByContentIdentifier(ContentIdentifier contentIdentifier) {
        return PathWorker.getPathByContentId(contentIdentifier);
    }
}
