package no.kantega.publishing.common.ao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath*:spring/testContext.xml")
public class JdbcPluginConfigurationAOTest {

    @Autowired
    private PluginConfigurationAO dao;

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
