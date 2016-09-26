package org.kantega.openaksess.plugins.database.controller;

import org.kantega.openaksess.plugins.database.dao.FormSubmissionDao;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteSubmissionAction extends AbstractController {
    FormSubmissionDao dao;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        int id = ServletRequestUtils.getIntParameter(httpServletRequest, "id", -1);

        if ( /*httpServletRequest.getMethod().equalsIgnoreCase("POST") && */ id != -1){
            dao.deleteFormSubmissionById(id);
        }
        return null;
    }

    public void setDao(FormSubmissionDao dao) {
        this.dao = dao;
    }

}
