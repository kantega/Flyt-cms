package no.kantega.openaksess.search.security;

import no.kantega.publishing.security.SecuritySession;
import no.kantega.search.api.search.SearchContext;

public class AksessSearchContext implements SearchContext {

    private final SecuritySession securitySession;
    private final int siteId;

    public AksessSearchContext(SecuritySession securitySession, int siteId) {
        this.securitySession = securitySession;
        this.siteId = siteId;
    }

    public SecuritySession getSecuritySession() {
        return securitySession;
    }

    public int getSiteId() {
        return siteId;
    }
}
