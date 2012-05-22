package no.kantega.publishing.api.taglibs.content;

import no.kantega.commons.log.Log;
import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.cache.DisplayTemplateCache;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.DisplayTemplate;
import no.kantega.publishing.common.util.RequestHelper;
import no.kantega.publishing.search.model.AksessSearchHit;
import no.kantega.publishing.spring.RootContext;
import no.kantega.search.result.SearchHit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class SearchViewTag extends TagSupport {
    private static final String SOURCE = "aksess.MiniViewTag";

    private SearchHit searchHit = null;
    private String var = "searchHit";
    private String defaultViewContent = "/WEB-INF/jsp/defaultviews/search.jsp";
    private String otherView = "/WEB-INF/jsp/defaultviews/search.jsp";

    public void setVar(String var) {
        this.var = var;
    }

    public void setSearchhit(SearchHit searchHit) {
        this.searchHit = searchHit;
    }

    public void setDefaultviewcontent(String defaultView) {
        this.defaultViewContent = defaultView;
    }

    public void setOtherview(String defaultView) {
        this.otherView = defaultView;
    }

    public int doStartTag() throws JspException {
        HttpServletRequest request   = (HttpServletRequest)pageContext.getRequest();
        SiteCache siteCache = (SiteCache) RootContext.getInstance().getBean("aksessSiteCache", SiteCache.class);

        Content currentPage = (Content)request.getAttribute("aksess_this");

        try {

            String template = otherView;

            if (searchHit != null && searchHit instanceof AksessSearchHit) {
                Content content = ((AksessSearchHit)searchHit).getContentObject();
                if (content != null) {
                    DisplayTemplate dt = DisplayTemplateCache.getTemplateById(content.getDisplayTemplateId());
                    if (dt != null && dt.getSearchView() != null && dt.getSearchView().length() > 0) {
                        template = dt.getSearchView();
                    } else {
                        template = defaultViewContent;
                    }
                    if (template != null && template.length() > 0) {
                        if (template.indexOf("$SITE") != -1) {
                            int siteId = currentPage.getAssociation().getSiteId();
                            Site site = siteCache.getSiteById(siteId);
                            String alias = site.getAlias();
                            template = template.replaceAll("\\$SITE", alias.substring(0, alias.length() - 1));
                        }

                    }
                    request.setAttribute("aksess_containingPage", currentPage);

                    // Ved å legge content på request'en med navn aksess_this vil malen kunne bruke standard tagger
                    RequestHelper.setRequestAttributes(request, content);
                }

                request.setAttribute(var, searchHit);

                pageContext.include(template);

                if (content != null) {
                    // Sett tilbake til denne siden
                    RequestHelper.setRequestAttributes(request, currentPage);

                    request.removeAttribute("aksess_containingPage");
                }
                request.removeAttribute(var);

            }
        } catch (Exception e) {
            Log.error(SOURCE, e, null, null);
        }

        return SKIP_BODY;
    }


    public int doEndTag() throws JspException {
        resetVars();
        return EVAL_PAGE;
    }

    private void resetVars() {
        defaultViewContent = "/WEB-INF/jsp/defaultviews/search.jsp";
        otherView = "/WEB-INF/jsp/defaultviews/search.jsp";
        var = "searchHit";
        searchHit = null;
    }
}
