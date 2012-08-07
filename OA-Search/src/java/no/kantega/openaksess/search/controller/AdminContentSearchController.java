package no.kantega.openaksess.search.controller;

import no.kantega.publishing.admin.viewcontroller.AdminController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdminContentSearchController extends AdminController {
    private ContentSearchController contentSearchController;
    private String view;

    @Override
    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return new ModelAndView(view, contentSearchController.handleRequest(request, response));
    }

    public void setContentSearchController(ContentSearchController contentSearchController) {
        this.contentSearchController = contentSearchController;
    }

    public void setView(String view) {
        this.view = view;
    }
}
