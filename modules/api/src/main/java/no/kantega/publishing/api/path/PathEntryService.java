package no.kantega.publishing.api.path;

import no.kantega.publishing.api.content.ContentIdentifier;

import java.util.List;

public interface PathEntryService {

    /**
     * @param contentIdentifier -
     * @return The PathEntries on the path to the Content identified by ContentIdentifier
     */
    public List<PathEntry> getPathEntriesByContentIdentifier(ContentIdentifier contentIdentifier);

    /**
     *
     * @param associationId -
     * @return The PathEntries on the path to the Content identified by associationId
     */
    public List<PathEntry> getPathEntriesByAssociationIdInclusive(Integer associationId);
}
