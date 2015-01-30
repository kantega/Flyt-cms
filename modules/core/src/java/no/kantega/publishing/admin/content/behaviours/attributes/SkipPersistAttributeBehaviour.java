
package no.kantega.publishing.admin.content.behaviours.attributes;

import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.attributes.Attribute;
import no.kantega.publishing.common.data.attributes.ListAttribute;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SkipPersistAttributeBehaviour implements PersistAttributeBehaviour {
    public void persistAttribute(Connection c, Content content, Attribute attribute) throws SQLException {
        // Do not persist
    }
}
