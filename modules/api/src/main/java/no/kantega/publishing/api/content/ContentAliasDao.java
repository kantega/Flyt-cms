package no.kantega.publishing.api.content;

import java.util.Set;

public interface ContentAliasDao {

    /**
     * @return list of all the defined aliases for content objects.
     */

    public Set<String> getAllAliases();
}
