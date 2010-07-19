package no.kantega.publishing.api.plugin;

import org.kantega.jexmec.Services;
import no.kantega.publishing.api.cache.SiteCache;

import javax.sql.DataSource;

/**
 * Services exposed to plugins by OpenAksess
 */
public interface OpenAksessServices extends Services {
    public SiteCache getSiteCache();

    public DataSource getDataSource(DataSourceName name);

    enum DataSourceName {
        aksessDataSource
    }

}
