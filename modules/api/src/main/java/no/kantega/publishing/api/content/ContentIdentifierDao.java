package no.kantega.publishing.api.content;

import java.util.List;

public interface ContentIdentifierDao {

    /**
     * Get <code>ContentIdentifier</code> by site and alias
     * @param siteId -
     * @param alias - should be ending and starting with /
     * @return the matching <code>ContentIdentifier</code>, or null if non existing
     */
    public ContentIdentifier getContentIdentifierBySiteIdAndAlias(int siteId, String alias);

    /**
     * @param siteId -
     * @param associationId -
     * @return Alias for <code>Content</code> with associationId in site with siteId, or null.
     */
    public String getAliasBySiteIdAndAssociationId(int siteId, int associationId);

    /**
     * @param alias for the <code>ContentIdentifier</code> wanted
     * @return list containing the matching <code>ContentIdentifier</code>s, or empty list.
     */
    public List<ContentIdentifier> getContentIdentifiersByAlias(String alias);
}
