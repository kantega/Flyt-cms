package no.kantega.publishing.common.ao.rowmapper;

import no.kantega.publishing.api.content.ContentIdentifier;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ContentIdentifierRowMapper implements RowMapper<ContentIdentifier> {
    @Override
    public ContentIdentifier mapRow(ResultSet rs, int i) throws SQLException {
        ContentIdentifier cid = ContentIdentifier.fromAssociationId(rs.getInt("AssociationId"));
        cid.setContentId(rs.getInt("ContentId"));
        cid.setSiteId(rs.getInt("SiteId"));
        return cid;
    }
}
