package no.kantega.publishing.common.ao;

import no.kantega.publishing.api.content.ContentAliasDao;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
public class ContentAliasDaoJdbcImpl extends JdbcDaoSupport implements ContentAliasDao {

    @Cacheable(value="AliasCache", key="#root.methodName")
    public Set<String> getAllAliases() {
        return new HashSet<>(getJdbcTemplate().queryForList("select alias from content, associations where content.contentId = associations.contentId and associations.IsDeleted = 0", String.class));
    }
}
