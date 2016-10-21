package no.kantega.publishing.admin.content.behaviours.attributes;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.Attribute;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class BasePersistAttributeBehaviour implements PersistAttributeBehaviour {
    @Override
    public void persistAttribute(Connection c, Content content, Attribute attribute) throws SQLException, SystemException {
        // Klassenavn angir navnet som brukes i databasen som attributttype
        String clsName = attribute.getClass().getName().toLowerCase();
        clsName = clsName.substring(clsName.lastIndexOf(".") + 1, clsName.lastIndexOf("attribute"));
        try(PreparedStatement st = c.prepareStatement("delete from contentattributes where ContentVersionId = ? AND AttributeType = ? AND DataType = ? AND  Name = ?")) {
            st.setInt(1, content.getVersionId());
            st.setString(2, clsName);
            st.setInt(3, attribute.getType().getDataTypeAsId());
            st.setString(4, attribute.getNameIncludingPath());
            st.execute();
        }
        try(PreparedStatement st = c.prepareStatement("insert into contentattributes (ContentVersionId, AttributeType, DataType, Name, Value) values (?,?,?,?,?)")) {

            st.setInt(1, content.getVersionId());
            st.setString(2, clsName);
            st.setInt(3, attribute.getType().getDataTypeAsId());
            st.setString(4, attribute.getNameIncludingPath());

            String value = getValuesAsString(attribute);
            st.setString(5, value);

            st.execute();
        }
    }

    public abstract String getValuesAsString(Attribute attribute);
}
