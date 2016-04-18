package no.kantega.publishing.admin.content.action;

import no.kantega.publishing.admin.AdminSessionAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * On save SaveContentAction return a redirect. This controller return json.
 */
public class SaveContentAction2 extends SaveContentAction {
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Override
    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView originalMAV = super.handleRequestInternal(request, response);
        Map<String, Object> model = new HashMap<>();

        Object errors = originalMAV.getModel().get("errors");
        if(errors != null){
            model.put("errors", errors);
            log.info("Errors when submitting content: {}", errors);
        } else {
            Object content = request.getSession().getAttribute(AdminSessionAttributes.CURRENT_NAVIGATE_CONTENT);
            model.put("content", content);
            log.info("Autosaved {}", content);
        }

        return new ModelAndView("/WEB-INF/jsp/admin/publish/saveContentResponse.jsp", model);
    }
}
