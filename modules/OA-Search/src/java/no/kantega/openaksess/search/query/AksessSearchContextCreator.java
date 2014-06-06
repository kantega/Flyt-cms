package no.kantega.openaksess.search.query;

import no.kantega.commons.util.URLHelper;
import no.kantega.openaksess.search.security.AksessSearchContext;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.search.api.search.context.SearchContextCreator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;

import static no.kantega.publishing.api.ContentUtil.tryGetFromRequest;

@Component
public class AksessSearchContextCreator implements SearchContextCreator, ApplicationContextAware {

    @Autowired
    private SiteCache siteCache;
    private ApplicationContext applicationContext;

    public AksessSearchContext getSearchContext(HttpServletRequest request) {
        String searchUrl = URLHelper.getServerURL(request) + URLHelper.getCurrentUrl(request);
        return new AksessSearchContext(applicationContext.getBean(SecuritySession.class), findSiteId(request), searchUrl);
    }

    private int findSiteId(HttpServletRequest request) {
        int siteId = 1;

        Content content = tryGetFromRequest(request);
        if (content != null) {
            siteId = content.getAssociation().getSiteId();
        } else if(request.getParameter("siteId") != null){
            siteId = ServletRequestUtils.getIntParameter(request, "siteId", siteId);
        }else {
            Site site = siteCache.getSiteByHostname(request.getServerName());
            if (site != null) {
                siteId = site.getId();
            }
        }
        return siteId;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
