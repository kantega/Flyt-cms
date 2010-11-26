package no.kantega.publishing.api.plugin;

import no.kantega.publishing.api.configuration.SystemConfiguration;
import no.kantega.security.api.identity.IdentityResolver;
import org.kantega.jexmec.Services;
import no.kantega.publishing.api.cache.SiteCache;

import javax.sql.DataSource;

/**
 * Services exposed to plugins by OpenAksess
 */
public interface OpenAksessServices extends Services {

    public SiteCache getSiteCache();

    public DataSource getDataSource(DataSourceName name);

    public IdentityResolver getIdentityResolver(IdentityResolverName name);

    public SystemConfiguration getSystemConfiguration();

    enum DataSourceName {
        aksessDataSource
    }

    enum IdentityResolverName {
        aksessIdentityResolver
    }

}
