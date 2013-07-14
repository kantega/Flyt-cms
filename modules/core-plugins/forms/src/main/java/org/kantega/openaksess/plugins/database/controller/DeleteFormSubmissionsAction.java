package org.kantega.openaksess.plugins.database.controller;

import org.kantega.openaksess.plugins.database.dao.FormSubmissionDao;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class DeleteFormSubmissionsAction extends AbstractController {
    FormSubmissionDao dao;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int formId = ServletRequestUtils.getIntParameter(request, "formId", -1);


        if (request.getMethod().equalsIgnoreCase("POST") && formId != -1) {
            dao.deleteFormSubmissionsByFormId(formId);
        }

        return null;
    }

    public void setDao(FormSubmissionDao dao) {
        this.dao = dao;
    }
}
