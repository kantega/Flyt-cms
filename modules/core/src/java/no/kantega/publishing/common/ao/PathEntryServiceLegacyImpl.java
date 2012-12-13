package no.kantega.publishing.common.ao;

import no.kantega.commons.log.Log;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.path.PathEntry;
import no.kantega.publishing.api.path.PathEntryService;
import no.kantega.publishing.common.service.impl.PathWorker;
import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PathEntryServiceLegacyImpl extends NamedParameterJdbcDaoSupport implements PathEntryService {

    @Override
    public List<PathEntry> getPathEntriesByContentIdentifier(ContentIdentifier contentIdentifier) {
        return PathWorker.getPathByContentId(contentIdentifier);
    }

    @Override
    public List<PathEntry> getPathEntriesByAssociationIdInclusive(Integer associationId) {
        List<PathEntry> pathEntries = Collections.emptyList();

        try {
            String path = getNamedParameterJdbcTemplate().queryForObject("select Path from associations where UniqueId = :associationId",
                    Collections.singletonMap("associationId", associationId), String.class);
            String replaceSlashAddCurrent = StringUtils.removeStart(path, "/").replace("/", ",") + associationId;
            Map<String,Object> associationIds = new HashMap<String, Object>();
            associationIds.put("associationIds", Arrays.asList(replaceSlashAddCurrent.split(",")));
            pathEntries = getNamedParameterJdbcTemplate().query("select contentversion.Title, associations.AssociationId from content, contentversion, associations  where content.ContentId = contentversion.ContentId and contentversion.IsActive = 1 and content.contentId = associations.contentId and associations.AssociationId in (:associationIds) order by associations.AssociationId", associationIds, rowMapper);
        } catch (DataAccessException e) {
            Log.error("PathEntryServiceLegacyImpl", e);
        }
        return pathEntries;
    }

    private final RowMapper<PathEntry> rowMapper = new RowMapper<PathEntry>() {
        @Override
        public PathEntry mapRow(ResultSet resultSet, int i) throws SQLException {
            return new PathEntry(resultSet.getInt("AssociationId"), resultSet.getString("Title"));
        }
    };
}
