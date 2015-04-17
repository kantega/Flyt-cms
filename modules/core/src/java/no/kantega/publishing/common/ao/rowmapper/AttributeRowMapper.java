package no.kantega.publishing.common.ao.rowmapper;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.admin.content.behaviours.attributes.UnPersistAttributeBehaviour;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.enums.AttributeDataType;
import no.kantega.publishing.common.factory.AttributeFactory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AttributeRowMapper implements RowMapper<Attribute> {
    private Content content;
    private AttributeFactory attributeFactory;

    private AttributeRowMapper() {
    }

    public AttributeRowMapper(Content content, AttributeFactory attributeFactory) {
        this.content = content;
        this.attributeFactory = attributeFactory;
    }

    public Attribute mapRow(ResultSet rs, int i) throws SQLException {
        Attribute attribute;
        try {
            attribute = attributeFactory.newAttribute(rs.getString("AttributeType"));
        } catch (Exception e) {
            throw new SystemException("Error creating attribute", e);
        }

        attribute.setName(rs.getString("Name"));

        UnPersistAttributeBehaviour behaviour = attribute.getFetchBehaviour();
        behaviour.unpersistAttribute(rs, attribute);

        content.addAttribute(attribute, AttributeDataType.getDataTypeAsEnum(rs.getInt("DataType")));

        return attribute;
    }
}
