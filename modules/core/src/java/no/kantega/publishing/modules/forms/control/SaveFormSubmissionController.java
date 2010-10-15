package no.kantega.publishing.modules.forms.control;

import no.kantega.publishing.controls.AksessController;
import no.kantega.publishing.modules.forms.util.FormSubmissionBuilder;
import no.kantega.publishing.modules.forms.util.FilledFormBuilder;
import no.kantega.publishing.modules.forms.model.FormSubmission;
import no.kantega.publishing.modules.forms.model.AksessContentForm;
import no.kantega.publishing.modules.forms.formdelivery.FormDeliveryService;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import no.kantega.publishing.modules.forms.model.Form;
import no.kantega.publishing.modules.forms.validate.FormError;

/**
 *
 */
public class SaveFormSubmissionController implements AksessController {

    private String description;
    private FormSubmissionBuilder formSubmissionBuilder;
    private FilledFormBuilder filledFormBuilder;
    private List<FormDeliveryService> formDeliveryServices;

    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();

        Content content = (Content) request.getAttribute("aksess_this");
        Form form = null;

        if (content != null) {
            form = new AksessContentForm(content);
        }

        if (form != null) {
            if (request.getMethod().equalsIgnoreCase("POST") && request.getParameter("isAksessFormSubmit") != null) {
                Map<String, String[]> values = new LinkedHashMap<String, String[]>(request.getParameterMap());
                addValues(values, request);

                FormSubmission formSubmission = formSubmissionBuilder.buildFormSubmission(values, form);

                // Validate formsubmission
                List<FormError> errors = formSubmission.getErrors();
                if (errors != null && errors.size() > 0) {
                    // errrors
                    model.put("hasErrors", Boolean.TRUE);
                    model.put("formSubmission", formSubmission);
                    form = filledFormBuilder.buildFilledForm(formSubmission);
                } else {

                    addUserInformation(formSubmission, request);

                    for (FormDeliveryService service : formDeliveryServices) {
                        service.deliverForm(formSubmission);
                    }
                    model.put("formSubmission", formSubmission);
                    model.put("hasSubmitted", Boolean.TRUE);
                    model.put("hasErrors",Boolean.FALSE);

                }
            } else {
                // Form is entered for the first time
                Map<String, String[]> prefillValues = new HashMap<String, String[]>();
                prefill(prefillValues, request);
                if (prefillValues.size() > 0) {
                    FormSubmission formSubmission = formSubmissionBuilder.buildFormSubmission(prefillValues, form);
                    if (formSubmission.getValues() != null) {
                        form = filledFormBuilder.buildFilledForm(formSubmission);
                    }
                }
            }

        }

        model.put("form", form);

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

    public void setFilledFormBuilder(FilledFormBuilder filledFormBuilder) {
        this.filledFormBuilder = filledFormBuilder;
    }

    public void setFormDeliveryServices(List<FormDeliveryService> formDeliveryServices) {
        this.formDeliveryServices = formDeliveryServices;
    }

    /**
     * Adds information about the user to the form submission.
     *
     * @param formSubmission a form submission
     * @param request a request
     */
    protected void addUserInformation(FormSubmission formSubmission, HttpServletRequest request) {
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
    }

    /**
     * Prefills values into the form.
     *
     * @param values a map of values
     * @param request a request
     */
    protected void prefill(Map<String, String[]> values, HttpServletRequest request) {
        Object prefillValues = request.getAttribute("aksessFormPrefillValues");
        if (prefillValues != null && prefillValues instanceof Map) {
            values.putAll((Map)prefillValues);
        }
    }

    /**
     * Adds additional values to or ovverides values in the form,
     *
     * @param values a map of values
     * @param request a request
     */
    protected void addValues(Map<String, String[]> values, HttpServletRequest request) {
        Map<String, String[]> prefillValues = (Map)request.getAttribute("aksessFormPrefillValues");
        if (prefillValues != null) {
            values.putAll(prefillValues);
        }
    }

}
