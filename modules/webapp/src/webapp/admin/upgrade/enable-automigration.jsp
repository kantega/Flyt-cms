<%@ page import="no.kantega.publishing.common.util.database.dbConnectionFactory" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %><%
    Connection c = dbConnectionFactory.getConnection();
    try {

        PreparedStatement updateSt = c.prepareStatement("create table oa_db_migrations (dbnamespace varchar(255) NOT NULL, version varchar(255) NOT NULL)");
        updateSt.executeUpdate();

        dbConnectionFactory.migrateDatabase(pageContext.getServletContext(), dbConnectionFactory.getDataSource());

    } finally {
        c.close();
    }
%>

Automatic database migration is now enabled and database migrated to last version.