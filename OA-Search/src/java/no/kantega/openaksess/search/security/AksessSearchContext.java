package no.kantega.openaksess.search.security;

import no.kantega.publishing.security.SecuritySession;
import no.kantega.search.api.search.SearchContext;

public class AksessSearchContext implements SearchContext {

    private final SecuritySession securitySession;

    public AksessSearchContext(SecuritySession securitySession) {
        this.securitySession = securitySession;
    }

    public SecuritySession getSecuritySession() {
        return securitySession;
    }
}
