package no.kantega.publishing.common.ao;

import no.kantega.publishing.api.content.ContentAliasDao;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ContentAliasDaoJdbcImpl extends JdbcDaoSupport implements ContentAliasDao {

    @Override
    public List<String> getAllAliases() {
        return getJdbcTemplate().queryForList("select alias from content", String.class);
    }
}
