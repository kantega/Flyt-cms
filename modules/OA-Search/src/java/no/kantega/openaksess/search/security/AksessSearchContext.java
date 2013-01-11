package no.kantega.openaksess.search.security;

import no.kantega.publishing.security.SecuritySession;
import no.kantega.search.api.search.SearchContext;

public class AksessSearchContext implements SearchContext {

    private final SecuritySession securitySession;
    private final int siteId;
    private final String searchUrl;

    public AksessSearchContext(SecuritySession securitySession, int siteId, String searchUrl) {
        this.securitySession = securitySession;
        this.siteId = siteId;
        this.searchUrl = searchUrl;
    }

    public SecuritySession getSecuritySession() {
        return securitySession;
    }

    public int getSiteId() {
        return siteId;
    }

    public String getSearchUrl() {
        return searchUrl;
    }
}
