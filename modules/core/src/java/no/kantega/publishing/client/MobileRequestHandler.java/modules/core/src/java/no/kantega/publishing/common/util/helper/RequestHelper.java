package no.kantega.publishing.common.util.helper;

import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.DisplayTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author Marvin B. Lillehaug <marvin.lillehaug@kantega.no>
 */
public interface RequestHelper {

    /**
     * Set attributes like locale and language.
     * @param request that attribues should be set on
     * @param content object for the page or null
     */
    public void setRequestAttributes(HttpServletRequest request, Content content);

    /**
     * Run the controllers that are defined for this template.
     * @param dt - the displaytemplate.
     * @param request - the current request.
     * @param response - the current response.
     * @return map with the result of running the controllers.
     * @throws Exception if a controller throws exception.
     */
    public Map<String, Object> runTemplateControllers(DisplayTemplate dt, HttpServletRequest request, HttpServletResponse response) throws Exception;
}
