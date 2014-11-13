package no.kantega.publishing.client;

import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.content.ContentIdentifierDao;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.spring.AksessAliasHandlerMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class AliasRequestHandler {
    private static Logger LOG = LoggerFactory.getLogger(AliasRequestHandler.class);

    @Autowired
    private ContentRequestHandler contentRequestHandler;

    @Autowired
    private ContentIdentifierDao contentIdentifierDao;

    @Autowired
    private SiteCache siteCache;

    public ModelAndView handleAlias(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, ContentNotFoundException {
        String alias = (String) request.getAttribute(AksessAliasHandlerMapping.HANDLED_OA_ALIAS);

        ContentIdentifier cid = getBestMatchingAlias(alias, request.getServerName());
        if(cid == null){
            throw new ContentNotFoundException(alias);
        }
        try {
            return contentRequestHandler.handleFromContentIdentifier(cid, request, response);
        } catch (Exception e) {
            LOG.error("Error handling alias {}, cid {}", alias, cid);
            LOG.error("Error handling alias", e);
            throw e;
        }
    }

    private ContentIdentifier getBestMatchingAlias(String alias, String serverName) {
        Site site = siteCache.getSiteByHostname(serverName);

        ContentIdentifier cid = null;
        if (site != null) {
            cid = contentIdentifierDao.getContentIdentifierBySiteIdAndAlias(site.getId(), alias);
        }
        if(cid == null){
            List<ContentIdentifier> cids = contentIdentifierDao.getContentIdentifiersByAlias(alias);
            if(cids.size() > 0){
                cid = cids.get(0);
                if(cids.size() > 1){
                    LOG.warn("More than one ContentIdentifier matched alias {}", alias);
                }
            }
        }
        return cid;
    }
}
