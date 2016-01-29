package no.kantega.publishing.controls.smoketest;

import no.kantega.publishing.api.content.attribute.AttributeDataType;
import no.kantega.publishing.api.taglibs.content.GetAttributeCommand;
import no.kantega.publishing.api.taglibs.content.util.AttributeTagHelper;
import no.kantega.publishing.common.cache.DisplayTemplateCache;
import no.kantega.publishing.common.cache.TemplateConfigurationCache;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentQuery;
import no.kantega.publishing.common.data.DisplayTemplate;
import no.kantega.publishing.common.data.SortOrder;
import no.kantega.publishing.common.data.enums.ContentProperty;
import no.kantega.publishing.common.data.enums.ContentType;
import no.kantega.publishing.common.service.ContentManagementService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


        List<Content> contents = new ArrayList<>();

        ContentManagementService cms = new ContentManagementService(request);

        if("true".equals(getServletContext().getInitParameter("testPagesEnabled"))) {
            String excludeFilter = request.getParameter("excludeFilter");

            List<DisplayTemplate> allTemplates = templateConfigurationCache.getTemplateConfiguration().getDisplayTemplates();
            List<DisplayTemplate> templates = filterTemplates(allTemplates, excludeFilter);
            for (DisplayTemplate template : templates) {
                ContentQuery query = new ContentQuery();
                query.setDisplayTemplate(template.getId());
                query.setMaxRecords(ServletRequestUtils.getIntParameter(request, "numberPrTemplate", 10));
                query.setSortOrder(new SortOrder(ContentProperty.PUBLISH_DATE));
                contents.addAll(cms.getContentList(query, false, false));
            }
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder= dbFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element pages = doc.createElement("pages");
            doc.appendChild(pages);

            for(Content content : contents) {
                if (content.getType() == ContentType.PAGE) {
                    Element page = doc.createElement("page");
                    page.setAttribute("url", content.getPath());

                    page.setAttribute("category", DisplayTemplateCache.getTemplateById(content.getDisplayTemplateId()).getName());

                    page.setAttribute("title", content.getTitle());

                    page.setAttribute("id", "contentId-" + content.getId());

                    pages.appendChild(page);
                }
            }

            response.setContentType("text/xml");
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(response.getOutputStream());

            transformer.transform(source, result);
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

        List<DisplayTemplate> filteredTemplates = new ArrayList<>();
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
        Map<String, String> filters = new HashMap<>();
        if (StringUtils.isNotEmpty(includeFilter)) {
            String[] filterArr = includeFilter.split(FILTER_SEPARATOR);
            for (String filter : filterArr) {
                String[] filterNameVal = filter.split("=");
                if (filterNameVal.length == 2) {
                    filters.put(filterNameVal[0], filterNameVal[1]);
                }
            }
        }
        return filters;
    }

    public void setTemplateConfigurationCache(TemplateConfigurationCache templateConfigurationCache) {
        this.templateConfigurationCache = templateConfigurationCache;
    }

}
