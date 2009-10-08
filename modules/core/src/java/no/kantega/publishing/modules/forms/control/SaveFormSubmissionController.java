package no.kantega.publishing.modules.forms.control;

import no.kantega.publishing.controls.AksessController;
import no.kantega.publishing.modules.forms.util.FormSubmissionBuilder;
import no.kantega.publishing.modules.forms.model.FormSubmission;
import no.kantega.publishing.modules.forms.model.AksessContentForm;
import no.kantega.publishing.modules.forms.formdelivery.FormDeliveryService;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
public class SaveFormSubmissionController implements AksessController {
    private String description;

    private FormSubmissionBuilder formSubmissionBuilder;
    private List<FormDeliveryService> formDeliveryServices;

    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();

        if (request.getMethod().equalsIgnoreCase("POST")) {
            Map<String, String[]> values = request.getParameterMap();

            Content content = (Content) request.getAttribute("aksess_this");
            if (content != null) {
                AksessContentForm form = new AksessContentForm(content);
                FormSubmission formSubmission = formSubmissionBuilder.buildFormSubmission(values, form);

                SecuritySession session = SecuritySession.getInstance(request);
                if (session.isLoggedIn()) {
                    User user = session.getUser();
                    formSubmission.setAuthenticatedIdentity(user.getId());
                    if (formSubmission.getSubmittedBy() == null) {
                        formSubmission.setSubmittedBy(user.getName());
                    }
                    if (formSubmission.getEmail() == null) {
                        formSubmission.setEmail(user.getEmail());
                    }
                }
                
                for (FormDeliveryService service : formDeliveryServices) {
                    service.deliverForm(formSubmission);
                }
                model.put("formSubmission", formSubmission);
                model.put("hasSubmitted", Boolean.TRUE);
            }
        }

        return model;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFormSubmissionBuilder(FormSubmissionBuilder formSubmissionBuilder) {
        this.formSubmissionBuilder = formSubmissionBuilder;
    }

    public void setFormDeliveryServices(List<FormDeliveryService> formDeliveryServices) {
        this.formDeliveryServices = formDeliveryServices;
    }
}
