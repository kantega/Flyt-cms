package no.kantega.publishing.security.interceptors;

import no.kantega.publishing.common.Aksess;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Interceptor that intercepts all requests to render an OpenAksess administration GUI view, e.g.
 * {@link no.kantega.publishing.admin.content.action.NavigateController}.
 */
public class AdminViewInterceptor extends HandlerInterceptorAdapter {

    /**
     * Adds a set of model attributes that are used by all OA admin views.
     * @param request request to render an admin view
     * @param response corresponding response
     * @param handler typically the controller or method that handled the request.
     * @param modelAndView the ModelAndView that the handler returned
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        if (modelAndView == null) {
            modelAndView = new ModelAndView();
        }

        Map<String, Object> model = modelAndView.getModel();
        if (model == null) {
            model = new HashMap<String, Object>();
        }
        model.put("aksess_locale", Aksess.getDefaultAdminLocale());

        String reqUri = request.getRequestURI();
        int start = reqUri.indexOf("/admin/");
        if (start != -1) {
            reqUri = reqUri.substring(start+"/admin/".length());
            reqUri = reqUri.substring(0, reqUri.indexOf("/"));
            model.put(reqUri + "Selected", "selected");
        }
    }


}
