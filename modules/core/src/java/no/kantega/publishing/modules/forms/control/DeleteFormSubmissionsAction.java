package no.kantega.publishing.modules.forms.control;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.log.Log;
import no.kantega.publishing.modules.forms.dao.FormSubmissionDao;

/**
 *
 */
public class DeleteFormSubmissionsAction  extends AbstractController {
    FormSubmissionDao dao;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request);
        int formId = param.getInt("formId");

        if (request.getMethod().equalsIgnoreCase("POST") && formId != -1) {
            Log.info(getClass().getName(), "Deleting formsubmissions for form:" + formId, null, null);
            dao.deleteFormSubmissionsByFormId(formId);
        }

        return null;
    }

    public void setDao(FormSubmissionDao dao) {
        this.dao = dao;
    }
}
