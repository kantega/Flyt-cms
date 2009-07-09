package no.kantega.publishing.admin.viewcontroller;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import no.kantega.publishing.common.Aksess;

/**
 *
 */
public class SimpleAdminController implements Controller {
    String view;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("aksess_locale", Aksess.getDefaultAdminLocale());
        return new ModelAndView(view, model);
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }
}
