package no.kantega.publishing.common.ao;

import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.path.PathEntry;
import no.kantega.publishing.api.path.PathEntryService;
import no.kantega.publishing.common.service.impl.PathWorker;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PathEntryServiceLegacyImpl extends NamedParameterJdbcDaoSupport implements PathEntryService {
    private static final Logger log = LoggerFactory.getLogger(PathEntryServiceLegacyImpl.class);

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
            Map<String,Object> associationIds = Collections.<String,Object>singletonMap("associationIds", Arrays.asList(replaceSlashAddCurrent.split(",")));

            pathEntries = getNamedParameterJdbcTemplate().query("select contentversion.Title, associations.AssociationId, content.contentTemplateId from content, contentversion, associations  where content.ContentId = contentversion.ContentId and contentversion.IsActive = 1 and content.contentId = associations.contentId and associations.AssociationId in (:associationIds) order by associations.Depth", associationIds, rowMapper);
        } catch (DataAccessException e) {
            log.error( e.getMessage());
        }
        return pathEntries;
    }

    private final RowMapper<PathEntry> rowMapper = new RowMapper<PathEntry>() {
        @Override
        public PathEntry mapRow(ResultSet resultSet, int i) throws SQLException {
            PathEntry pathEntry = new PathEntry(resultSet.getInt("AssociationId"), resultSet.getString("Title"));
            pathEntry.setContentTemplateId(resultSet.getInt("contentTemplateId"));
            return pathEntry;
        }
    };
}
