package no.kantega.publishing.api.content;

import java.util.List;

public interface ContentAliasDao {

    /**
     * @return list of all the defined aliases for content objects.
     */
    public List<String> getAllAliases();
}
