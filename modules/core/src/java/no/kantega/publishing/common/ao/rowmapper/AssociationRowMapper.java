package no.kantega.publishing.common.ao.rowmapper;

import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.AssociationCategory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AssociationRowMapper implements RowMapper<Association> {
    public Association mapRow(ResultSet rs, int i) throws SQLException {
        Association association = new Association();
        association.setId(rs.getInt("UniqueId"));
        association.setAssociationId(rs.getInt("AssociationId"));
        association.setContentId(rs.getInt("ContentId"));
        association.setParentAssociationId(rs.getInt("ParentAssociationId"));
        association.setCategory(new AssociationCategory(rs.getInt("Category")));
        association.setSiteId(rs.getInt("SiteId"));
        association.setSecurityId(rs.getInt("SecurityId"));
        association.setAssociationtype(rs.getInt("Type"));
        association.setPriority(rs.getInt("Priority"));
        association.setPath(rs.getString("Path"));
        association.setDepth(rs.getInt("Depth"));
        association.setDeleted(rs.getInt("IsDeleted") == 1);
        association.setNumberOfViews(rs.getInt("NumberOfViews"));
        return association;
    }
}
