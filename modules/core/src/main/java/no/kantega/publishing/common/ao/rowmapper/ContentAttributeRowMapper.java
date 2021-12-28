package no.kantega.publishing.common.ao.rowmapper;

import no.kantega.publishing.common.ao.ContentAOHelper;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.Attribute;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ContentAttributeRowMapper implements RowMapper<Attribute> {

    private final Content content;

    public ContentAttributeRowMapper(Content content) {
        this.content = content;
    }

    @Override
    public Attribute mapRow(ResultSet rs, int rowNum) throws SQLException {
        ContentAOHelper.addAttributeFromRS(content, rs);

        return null;
    }
}