package no.kantega.openaksess.search.query;

import no.kantega.commons.util.URLHelper;
import no.kantega.openaksess.search.security.AksessSearchContext;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.security.SecuritySession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class AksessSearchContextCreator {

    @Autowired
    private SiteCache siteCache;

    public AksessSearchContext getSearchContext(HttpServletRequest request) {
        String searchUrl = URLHelper.getServerURL(request) + URLHelper.getCurrentUrl(request);
        return new AksessSearchContext(SecuritySession.getInstance(request), findSiteId(request), searchUrl);
    }

    private int findSiteId(HttpServletRequest request) {
        int siteId = 1;

        Content content = (Content)request.getAttribute("aksess_this");
        if (content != null) {
            siteId = content.getAssociation().getSiteId();
        } else {
            Site site = siteCache.getSiteByHostname(request.getServerName());
            if (site != null) {
                siteId = site.getId();
            }
        }
        return siteId;
    }
}
