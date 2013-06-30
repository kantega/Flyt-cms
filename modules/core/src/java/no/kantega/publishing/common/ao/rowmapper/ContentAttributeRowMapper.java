package no.kantega.publishing.common.ao.rowmapper;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.admin.content.behaviours.attributes.UnPersistAttributeBehaviour;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.ListAttribute;
import no.kantega.publishing.common.data.attributes.RepeaterAttribute;
import no.kantega.publishing.common.factory.AttributeFactory;
import no.kantega.publishing.common.factory.ClassNameAttributeFactory;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContentAttributeRowMapper implements RowMapper<Attribute> {
    private static final Logger log = LoggerFactory.getLogger(ContentAttributeRowMapper.class);

    private final Content content;

    public ContentAttributeRowMapper(Content content) {
        this.content = content;
    }

    @Override
    public Attribute mapRow(ResultSet rs, int rowNum) throws SQLException {
        String attributeType = StringUtils.defaultIfEmpty(rs.getString("AttributeType"), "Text");

        int attributeDataType = rs.getInt("DataType");

        String attributeNameIncludingPath = rs.getString("Name");
        String value = rs.getString("Value");

        List<Attribute> attributes = content.getAttributes(attributeDataType);
        Attribute parentAttribute = null;

        String path[] = attributeNameIncludingPath.split("\\].");
        for (String pathElement : path) {
            if (pathElement.contains("[")) {
                // Repeater attribute
                String rowStr = pathElement.substring(pathElement.indexOf("[") + 1, pathElement.length());
                int row = Integer.parseInt(rowStr, 10);
                pathElement = pathElement.substring(0, pathElement.indexOf("["));

                // Check if repeater has been created
                RepeaterAttribute repeater = createOrGetExistingRepeaterAttributeByName(attributes, pathElement, attributeDataType);
                repeater.setParent(parentAttribute);

                parentAttribute = repeater;

                addCorrectNumberOfRows(parentAttribute, row, repeater);


                // Get correct row
                attributes = repeater.getRow(row);
            } else {
                // Normal attribute
                AttributeFactory factory = new ClassNameAttributeFactory();
                Attribute attribute = null;
                try {
                    attribute = factory.newAttribute(attributeType);
                } catch (Exception e) {
                    log.error("Error instantiating attribute " + attributeType, e);
                    throw new SystemException("Feil ved oppretting av klasse for attributt" + attributeType, e);
                }
                attribute.setParent(parentAttribute);

                attributes.add(attribute);

                value = removeExtraCharsFromListAttribute(value, attribute);
                attribute.setValue(value);
                attribute.setName(pathElement);

                UnPersistAttributeBehaviour behaviour = attribute.getFetchBehaviour();
                behaviour.unpersistAttribute(rs, attribute);

                return null;

            }

        }
        return null;
    }

    private void addCorrectNumberOfRows(Attribute parentAttribute, int row, RepeaterAttribute repeater) {
        List<Attribute> attributes;// Add number of necessary rows
        while (repeater.getNumberOfRows() < row + 1) {
            attributes = new ArrayList<>();
            repeater.addRow(attributes);
            repeater.setParent(parentAttribute);
        }
    }

    private String removeExtraCharsFromListAttribute(String value, Attribute attribute) {
        if (attribute instanceof ListAttribute && value != null && value.length() > 0) {
            // Lists are stored with , in front and end to make them searchable via SQL
            if (value.charAt(0) == ',') {
                value = value.substring(1, value.length());
            }
            if (value.charAt(value.length() - 1) == ',') {
                value = value.substring(0, value.length() - 1);
            }
        }
        return value;
    }

    private RepeaterAttribute createOrGetExistingRepeaterAttributeByName(List<Attribute> attributes, String pathElement, int attributeDataType) {
        for (Attribute attribute : attributes) {
            if (attribute.getName().equals(pathElement) && attribute instanceof RepeaterAttribute) {
                return (RepeaterAttribute)attribute;
            }
        }

        RepeaterAttribute repeater = new RepeaterAttribute();
        repeater.setName(pathElement);
        repeater.setType(attributeDataType);
        attributes.add(repeater);

        return repeater;
    }

}