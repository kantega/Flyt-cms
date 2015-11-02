package no.kantega.publishing.admin.content.action;

import no.kantega.publishing.admin.AdminSessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * On save SaveContentAction return a redirect. This controller return json.
 */
public class SaveContentAction2 extends SaveContentAction {
    @Override
    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView originalMAV = super.handleRequestInternal(request, response);
        Map<String, Object> model = new HashMap<>();

        Object errors = originalMAV.getModel().get("errors");
        if(errors != null){
            model.put("errors", errors);
        } else {
            model.put("content", request.getSession().getAttribute(AdminSessionAttributes.CURRENT_NAVIGATE_CONTENT));
        }

        return new ModelAndView("/WEB-INF/jsp/admin/publish/saveContentResponse.jsp", model);
    }
}
