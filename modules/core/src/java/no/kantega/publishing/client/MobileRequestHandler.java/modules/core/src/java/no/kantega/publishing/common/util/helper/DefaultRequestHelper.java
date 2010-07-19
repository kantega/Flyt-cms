package no.kantega.publishing.common.util.helper;

import no.kantega.publishing.api.cache.SiteCache;
import no.kantega.publishing.api.model.Site;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.DisplayTemplate;
import no.kantega.publishing.common.data.DisplayTemplateControllerId;
import no.kantega.publishing.common.data.enums.Language;
import no.kantega.publishing.controls.AksessController;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Marvin B. Lillehaug <marvin.lillehaug@kantega.no>
 */
public class DefaultRequestHelper implements RequestHelper, BeanFactoryAware {
    private SiteCache siteCache;
    private BeanFactory beanFactory;


    public void setRequestAttributes(HttpServletRequest request, Content content) {
        if (content == null) {
            Site site = siteCache.getSiteByHostname(request.getServerName());
            if (site != null){
                String alias = site.getAlias();
                request.setAttribute("aksess_site", alias);
            }

        } else {
            int siteId = content.getAssociation().getSiteId();
            Site site = siteCache.getSiteById(siteId);
            String alias = site.getAlias();
            request.setAttribute("aksess_locale", (Language.getLanguageAsLocale(content.getLanguage())));
            request.setAttribute("aksess_language", new Integer(content.getLanguage()));
            request.setAttribute("aksess_site", alias);
            request.setAttribute("aksess_this", content);
        }
    }

    public Map<String, Object> runTemplateControllers(DisplayTemplate dt, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        // Run all controllers
        if(dt.getControllers() != null) {
            for (DisplayTemplateControllerId displayTemplateController : dt.getControllers()) {
                AksessController aksessController = (AksessController) beanFactory.getBean(displayTemplateController.getId(), AksessController.class);
                model.putAll(aksessController.handleRequest(request, response));
            }
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Autowired
    public void setSiteCache(SiteCache siteCache) {
        this.siteCache = siteCache;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
