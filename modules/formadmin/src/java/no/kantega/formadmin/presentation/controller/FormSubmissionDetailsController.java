package no.kantega.formadmin.presentation.controller;

import no.kantega.formengine.administration.FormAdministration;
import no.kantega.formengine.model.FormSubmission;
import no.kantega.formengine.model.FormSubmissionQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 */
@Controller
public class FormSubmissionDetailsController extends FormAdminBaseController {

    private FormAdministration formAdministration;

    @Autowired
    public FormSubmissionDetailsController(FormAdministration formAdministration) {
        this.formAdministration = formAdministration;
    }

    /**
     * 
     * @param model
     * @param formSubmissionId
     * @param request
     * @return
     */
    @RequestMapping("/viewdetails")
    public String viewDetails(Model model, @RequestParam("id") Integer formSubmissionId, HttpServletRequest request) {
        FormSubmissionQuery query = formAdministration.createFormSubmissionQuery();
        query.setFormSubmissionId(formSubmissionId);
        List<FormSubmission> submissions = formAdministration.searchFormSubmissions(query, getIdentityFromRequest(request));

        if (submissions != null && submissions.size() > 0) {
            model.addAttribute("formSubmission", submissions.get(0));
        }

        return "formsubmissiondetails";
    }

}
