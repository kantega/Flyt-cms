package no.kantega.publishing.client;

import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.ContentQuery;
import no.kantega.publishing.common.data.DisplayTemplate;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.service.factory.AksessServiceFactory;
import no.kantega.publishing.common.util.helper.RequestHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author Marvin B. Lillehaug <marvin.lillehaug@kantega.no>
 */
@Controller
public class MobileRequestHandler {
    private AksessServiceFactory serviceFactory;
    private RequestHelper requestHelper;
    private SiteCache siteCache;

    @RequestMapping("/mobile")
    public String handle(@RequestParam Integer thisId, HttpServletRequest request, HttpServletResponse response, Model model ) throws Exception {
        String view = "/WEB-INF/jsp/mobil/mobil.jsp";

        ContentManagementService cms = serviceFactory.getContentManagementService(request) ;

        ContentIdentifier parent = new ContentIdentifier();
        parent.setAssociationId(thisId);

        Content content = cms.getContent(parent);
        if (content != null && content.getDisplayTemplateId() > 0) {
            DisplayTemplate template = cms.getDisplayTemplate(content.getDisplayTemplateId());
            if (template.getMobileView() != null && template.getMobileView().length() > 0) {
                view = template.getMobileView();
            }else if (template.getView() != null && template.getView().length() > 0) {
                view = template.getView();
            }
            model.addAttribute("aksess_this", content);
            Map<String,Object> controllersResult = requestHelper.runTemplateControllers(template, request, response);
            model.addAllAttributes(controllersResult);
        }
        int siteId = content.getAssociation().getSiteId();
        Site site = siteCache.getSiteById(siteId);
        String alias = site.getAlias();
        // If template filename contains macro $SITE, replace with correct site
        if (view.indexOf("$SITE") != -1) {
            view = view.replaceAll("\\$SITE", alias);
        }

        ContentQuery query = new ContentQuery();
        query.setAssociatedId(parent);
        model.addAttribute("contentQuery", query);

        return view;
    }

    @Autowired
    public void setServiceFactory(AksessServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Autowired
    public void setRequestHelper(RequestHelper requestHelper) {
        this.requestHelper = requestHelper;
    }

    @Autowired
    public void setSiteCache(SiteCache siteCache) {
        this.siteCache = siteCache;
    }
}
