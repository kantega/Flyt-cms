package no.kantega.openaksess.search.controller;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdminMultimediaSearchController extends AbstractController {
    private MultimediaSearchController multimediaSearchController;
    private String view;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        return new ModelAndView(view, multimediaSearchController.handleRequest(httpServletRequest, httpServletResponse));
    }

    public void setMultimediaSearchController(MultimediaSearchController multimediaSearchController) {
        this.multimediaSearchController = multimediaSearchController;
    }

    public void setView(String view) {
        this.view = view;
    }
}
