package no.kantega.publishing.common.ao;

import no.kantega.publishing.test.database.DerbyDatabaseCreator;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 *
 */
public class JdbcPluginConfigurationAOTest {
    private PluginConfigurationAO dao;

    @Before
    public void setup() {
        
        String create= "create table pluginConfiguration (pluginNamespace varchar(255) not null, configName varchar(512) NOT NULL, configValue CLOB NOT NULL);";
        
        DataSource dataSource = new DerbyDatabaseCreator("aksess", new ByteArrayInputStream(create.getBytes())).createDatabase();
        
        JdbcPluginConfigurationAO dao = new JdbcPluginConfigurationAO();
        dao.setDataSource(dataSource);
        this.dao = dao;
                
    }

    @Test
    public void shouldReadWrittenProperties() {
        assertNull(dao.getProperty("pluginA", "nameA"));

        dao.setProperty("pluginA", "nameA", "valueA");
        dao.setProperty("pluginB", "nameB", "valueB");

        assertEquals("valueA", dao.getProperty("pluginA", "nameA"));
        assertEquals("valueB", dao.getProperty("pluginB", "nameB"));
        
        dao.setProperty("pluginA", "nameA", "valueA2");

        assertEquals("valueA2", dao.getProperty("pluginA", "nameA"));
    }
}
