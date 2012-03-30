package no.kantega.publishing.modules.forms.control;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public class FormSubmissionsExportExcelController extends AbstractController {
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Content-Disposition", "attachment; filename=\"OpenAksess_export.xls\"");
        return new ModelAndView("FormSubmissionsExportExcelView");
    }
}
