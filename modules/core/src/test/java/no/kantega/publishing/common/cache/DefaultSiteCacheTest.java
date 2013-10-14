package no.kantega.publishing.common.cache;

import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.model.Site;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class DefaultSiteCacheTest {

    private SiteCache siteCache;

    @Before
    public void setUp() throws Exception {
        siteCache = new DefaultSiteCache();
    }

    @Test
    public void shouldReturnSingleDefinedSiteAsDefault(){
        Site site = new Site();
        ReflectionTestUtils.setField(siteCache, "sites", asList(site));

        assertEquals("SiteCache did not return correct default site", site, siteCache.getDefaultSite());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowWhenNoSitesDefined(){
        ReflectionTestUtils.setField(siteCache, "sites", new ArrayList<Site>());
        siteCache.getDefaultSite();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowWhenSeveralDefaultSitesDefined(){
        Site s1 = new Site();
        s1.setDefault(true);
        Site s2 = new Site();
        Site s3 = new Site();
        Site s4 = new Site();
        s4.setDefault(true);

        ReflectionTestUtils.setField(siteCache, "sites", asList(s1, s2, s3, s4));
        siteCache.getDefaultSite();
    }

    @Test
    public void shouldGetFirstSiteWhenNoDefaultDefined(){
        Site s1 = new Site();
        Site s2 = new Site();
        Site s3 = new Site();
        Site s4 = new Site();

        ReflectionTestUtils.setField(siteCache, "sites", asList(s1, s2, s3, s4));
        assertEquals("SiteCache did not return correct default site", s1, siteCache.getDefaultSite());
    }

    @Test
    public void shouldGetDefaultSiteWhenDefaultDefined(){
        Site s1 = new Site();
        Site s2 = new Site();
        Site s3 = new Site();
        s3.setDefault(true);
        Site s4 = new Site();

        ReflectionTestUtils.setField(siteCache, "sites", asList(s1, s2, s3, s4));
        assertEquals("SiteCache did not return correct default site", s3, siteCache.getDefaultSite());
    }
}
