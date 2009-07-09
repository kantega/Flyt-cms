package no.kantega.publishing.admin.viewcontroller.navigate;

import no.kantega.publishing.admin.viewcontroller.AdminController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Author: Kristian Lier Selnæs, Kantega AS
 * Date: 01.jul.2009
 * Time: 15:04:08
 */
public class NavigateController extends AdminController {

    private String viewName;

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) {

        return new ModelAndView(viewName);
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }
}
