package no.kantega.openaksess.search.controller;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdminContentSearchController extends AbstractController {
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
