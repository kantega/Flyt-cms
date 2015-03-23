package no.kantega.publishing.controls.smoketest;

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
import org.apache.commons.lang3.StringUtils;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Creates an xml containing a list of pages where each page is the published page for each display template.
 * The xml produced:
 * <pre>
 * &lt;pages&gt;
 *     &lt;page category="" title="" id="" url=""/&gt;
 * &lt;/pages&gt;
 * </pre>
 */
public class TestPagesController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(TestPagesController.class);

    private TemplateConfigurationCache templateConfigurationCache;
    private static final String FILTER_SEPARATOR = "\\|";


    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {


        List<Content> contents = new ArrayList<Content>();

        ContentManagementService cms = new ContentManagementService(request);

        if("true".equals(getServletContext().getInitParameter("testPagesEnabled"))) {
            String excludeFilter = request.getParameter("excludeFilter");

            List<DisplayTemplate> allTemplates = templateConfigurationCache.getTemplateConfiguration().getDisplayTemplates();
            List<DisplayTemplate> templates = filterTemplates(allTemplates, excludeFilter);
            for (DisplayTemplate template : templates) {
                ContentQuery query = new ContentQuery();
                query.setDisplayTemplate(template.getId());

                contents.addAll(cms.getContentList(query, 1, new SortOrder(ContentProperty.PUBLISH_DATE)));
            }
            Element pages = new Element("pages");

            for(Content content : contents) {
                if (content.getType() == ContentType.PAGE) {
                    Element page = new Element("page");
                    final GetAttributeCommand cmd = new GetAttributeCommand();
                    cmd.setAttributeType(AttributeDataType.CONTENT_DATA);

                    cmd.setName("url");
                    page.setAttribute("url", AttributeTagHelper.getAttribute(content, cmd).substring(request.getContextPath().length()));

                    cmd.setName("displaytemplate");
                    page.setAttribute("category", AttributeTagHelper.getAttribute(content, cmd));

                    page.setAttribute("title", content.getTitle());

                    page.setAttribute("id", "contentId-" + content.getId());

                    pages.addContent(page);
                }
            }

            response.setContentType("text/xml");
            XMLOutputter outputter = new XMLOutputter();
            try {
                outputter.output(pages, response.getOutputStream());
            }
            catch (IOException e) {
                log.error("", e);
            }
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }

        return null;
    }

    private List<DisplayTemplate> filterTemplates(List<DisplayTemplate> allTemplates, String excludeFilter) {
        Map<String, String> excludes = parseFilter(excludeFilter);

        if (excludes.isEmpty()) {
            return allTemplates;
        }

        List<DisplayTemplate> filteredTemplates = new ArrayList<DisplayTemplate>();
        for (DisplayTemplate template : allTemplates) {
            if (!isExcluded(excludes, template)) {
                filteredTemplates.add(template);
            }
        }
        return filteredTemplates;
    }

    private boolean isExcluded(Map<String, String> excludes, DisplayTemplate template) {
        Enumeration<String> propertyNames = (Enumeration<String>) template.getProperties().propertyNames();
        for (String property : Collections.list(propertyNames)) {
            if (excludes.containsKey(property) && template.getProperties().getProperty(property).equals(excludes.get(property))) {
                return true;
            }
        }
        return false;
    }

    private Map<String, String> parseFilter(String includeFilter) {
        Map<String, String> filters = new HashMap<String, String>();
        if (StringUtils.isNotEmpty(includeFilter)) {
            String[] filterArr = includeFilter.split(FILTER_SEPARATOR);
            if (filterArr != null) {
                for (String filter : filterArr) {
                    String[] filterNameVal = filter.split("=");
                    if (filterNameVal != null && filterNameVal.length == 2) {
                        filters.put(filterNameVal[0], filterNameVal[1]);
                    }
                }
            }
        }
        return filters;
    }

    public void setTemplateConfigurationCache(TemplateConfigurationCache templateConfigurationCache) {
        this.templateConfigurationCache = templateConfigurationCache;
    }

}
