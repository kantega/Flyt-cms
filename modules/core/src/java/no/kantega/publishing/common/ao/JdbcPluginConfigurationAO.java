package no.kantega.publishing.common.ao;

import no.kantega.publishing.api.plugin.PluginConfigurationAO;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

/**
 *
 */
public class JdbcPluginConfigurationAO implements PluginConfigurationAO {
    private JdbcTemplate template;

    public String getProperty(String pluginUid, String name) {
        List<String> values = template.queryForList("select configValue from pluginConfiguration where configName=? and pluginNamespace=?",
                new Object[] {name, pluginUid}, String.class);
        if(values.isEmpty()) {
            return null;
        } else if(values.size() == 1) {
            return values.get(0);
        } else {
            throw new IllegalStateException("Expected zero or a single row querying for property '" + name +"' in plugin '" + pluginUid +"'");
        }

    }

    public synchronized void setProperty(String pluginUid, String name, String value) {
        int affected = template.update("update pluginConfiguration set configValue=? where configName = ? and pluginNamespace = ?", value, name, pluginUid);
        if(affected == 0) {
            template.update("insert  into pluginConfiguration(pluginNamespace, configName, configValue) VALUES (?, ?, ?)", pluginUid, name, value);
        }
    }

    public void setDataSource(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }
}
