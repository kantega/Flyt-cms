package no.kantega.publishing.controls.smoketest;

import no.kantega.publishing.common.cache.TemplateConfigurationCache;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentQuery;
import no.kantega.publishing.common.data.DisplayTemplate;
import no.kantega.publishing.common.data.SortOrder;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.service.ContentManagementService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class SmokeTestPagesController extends AbstractController {

    private TemplateConfigurationCache templateConfigurationCache;

    private String view;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        ModelAndView model = new ModelAndView(view);

        List<Content> contents = new ArrayList<Content>();

        ContentManagementService cms = new ContentManagementService(request);

        if("true".equals(getServletContext().getInitParameter("smokeTestEnabled"))) {
            final List<DisplayTemplate> templates = templateConfigurationCache.getTemplateConfiguration().getDisplayTemplates();
            for (DisplayTemplate template : templates) {
                ContentQuery query = new ContentQuery();
                query.setDisplayTemplate(template.getId());

                contents.addAll(cms.getContentList(query, 1, new SortOrder(ContentProperty.PUBLISH_DATE)));
            }
            model.addObject("pages", contents);
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }

        return model;
    }

    public void setTemplateConfigurationCache(TemplateConfigurationCache templateConfigurationCache) {
        this.templateConfigurationCache = templateConfigurationCache;
    }

    public void setView(String view) {
        this.view = view;
    }
}
