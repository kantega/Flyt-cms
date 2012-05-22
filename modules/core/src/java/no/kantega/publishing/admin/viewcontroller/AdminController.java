package no.kantega.publishing.admin.viewcontroller;

import no.kantega.publishing.common.Aksess;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Kristian Lier Selnæs, Kantega AS
 * Date: 02.jul.2009
 * Time: 08:48:04
 */
public abstract class AdminController implements Controller {


    public final ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView modelAndView = handleRequestInternal(request, response);
        Map model = modelAndView.getModel();
        if (model == null) {
            model = new HashMap();
        }
        model.put("aksess_locale", Aksess.getDefaultAdminLocale());

        String reqUri = request.getRequestURI();
        int start = reqUri.indexOf("/admin/");
        if (start != -1) {
            reqUri = reqUri.substring(start+"/admin/".length());
            reqUri = reqUri.substring(0, reqUri.indexOf("/"));
            model.put(reqUri + "Selected", "selected");
        }
        return modelAndView;
    }

    public abstract ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception;

}
