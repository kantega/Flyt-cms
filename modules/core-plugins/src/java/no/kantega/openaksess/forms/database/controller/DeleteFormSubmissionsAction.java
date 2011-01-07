package no.kantega.openaksess.forms.database.controller;

import no.kantega.openaksess.forms.database.dao.FormSubmissionDao;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

import no.kantega.commons.client.util.RequestParameters;

public class DeleteFormSubmissionsAction  extends AbstractController {
    FormSubmissionDao dao;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request);
        int formId = param.getInt("formId");

        if (request.getMethod().equalsIgnoreCase("POST") && formId != -1) {
            dao.deleteFormSubmissionsByFormId(formId);
        }

        return null;
    }

    public void setDao(FormSubmissionDao dao) {
        this.dao = dao;
    }
}
