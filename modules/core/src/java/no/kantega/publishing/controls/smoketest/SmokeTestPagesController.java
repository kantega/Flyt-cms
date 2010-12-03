package no.kantega.publishing.controls.smoketest;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.log.Log;
import no.kantega.publishing.api.taglibs.content.GetAttributeCommand;
import no.kantega.publishing.api.taglibs.content.util.AttributeTagHelper;
import no.kantega.publishing.common.cache.TemplateConfigurationCache;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentQuery;
import no.kantega.publishing.common.data.DisplayTemplate;
import no.kantega.publishing.common.data.SortOrder;
import no.kantega.publishing.common.data.enums.AttributeDataType;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.data.enums.ContentType;
import no.kantega.publishing.common.service.ContentManagementService;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 *
 */
public class SmokeTestPagesController extends AbstractController {

    private TemplateConfigurationCache templateConfigurationCache;


    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        List<Integer> excludedTemplates = Collections.emptyList();
        String excludedTemplatesParam = request.getParameter("excludedTemplates");
        if (excludedTemplatesParam != null && excludedTemplatesParam.trim().length() > 0) {
            String[] excludedIds = excludedTemplatesParam.split(",");
            excludedTemplates = new ArrayList<Integer>();
            for (String excludedId : excludedIds) {
                try {
                    excludedTemplates.add(Integer.parseInt(excludedId));
                } catch (NumberFormatException e) {
                    Log.info(this.getClass().getName(), "Could not exclude template with id: " + excludedId);
                }
            }
        }

        List<Content> contents = new ArrayList<Content>();

        ContentManagementService cms = new ContentManagementService(request);

        if("true".equals(getServletContext().getInitParameter("smokeTestEnabled"))) {
            final List<DisplayTemplate> templates = templateConfigurationCache.getTemplateConfiguration().getDisplayTemplates();
            for (DisplayTemplate template : templates) {
                ContentQuery query = new ContentQuery();
                query.setDisplayTemplate(template.getId());

                contents.addAll(cms.getContentList(query, 1, new SortOrder(ContentProperty.PUBLISH_DATE)));
            }
            Element pages = new Element("pages");
            Element includes = new Element("includes");
            pages.addContent(includes);

            for(Content content : contents) {
                if (content.getType() == ContentType.PAGE && (excludedTemplates == null || !excludedTemplates.contains(content.getDisplayTemplateId()))) {
                    Element page = new Element("page");

                    final GetAttributeCommand cmd = new GetAttributeCommand();
                    cmd.setAttributeType(AttributeDataType.CONTENT_DATA);
                    cmd.setName("url");
                    page.setAttribute("url", AttributeTagHelper.getAttribute(content, cmd).substring(request.getContextPath().length()));

                    cmd.setName("displaytemplate");
                    page.setAttribute("category", AttributeTagHelper.getAttribute(content, cmd));

                    page.setAttribute("title", content.getTitle());

                    page.setAttribute("id", "contentId-" + content.getId());

                    includes.addContent(page);
                }
            }

            response.setContentType("text/xml");
            XMLOutputter outputter = new XMLOutputter();
            try {
                outputter.output(pages, response.getOutputStream());
            }
            catch (IOException e) {
                Log.error(this.getClass().getName(), "An error occured when attempting to write smoke test config.", null, null);
                System.err.println(e);
            }
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }

        return null;
    }

    public void setTemplateConfigurationCache(TemplateConfigurationCache templateConfigurationCache) {
        this.templateConfigurationCache = templateConfigurationCache;
    }

}
