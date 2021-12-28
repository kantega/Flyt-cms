package no.kantega.publishing.modules.forms.control;

import no.kantega.commons.exception.ConfigurationException;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.configuration.SystemConfiguration;
import no.kantega.publishing.api.forms.delivery.FormDeliveryService;
import no.kantega.publishing.api.forms.model.Form;
import no.kantega.publishing.api.forms.model.FormSubmission;
import no.kantega.publishing.api.plugin.OpenAksessPlugin;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.controls.AksessController;
import no.kantega.publishing.modules.forms.model.AksessContentForm;
import no.kantega.publishing.modules.forms.util.FilledFormBuilder;
import no.kantega.publishing.modules.forms.util.FormSubmissionBuilder;
import no.kantega.publishing.modules.forms.validate.FormError;
import no.kantega.publishing.modules.forms.validate.FormSubmissionValidator;
import no.kantega.publishing.modules.mailsender.MailSender;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.User;
import org.kantega.jexmec.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static no.kantega.publishing.api.ContentUtil.tryGetFromRequest;

/**
 *
 */
public class SaveFormSubmissionController implements AksessController {
    private static final Logger log = LoggerFactory.getLogger(SaveFormSubmissionController.class);

    private String description;
    private FormSubmissionBuilder formSubmissionBuilder;
    private FilledFormBuilder filledFormBuilder;
    private FormSubmissionValidator formSubmissionValidator;
    private String formDeliveryServiceIds;
    private PluginManager<OpenAksessPlugin> pluginManager;
    private String mailConfirmationSubject;
    private String mailConfirmationTemplate;

    private SystemConfiguration configuration;

    public Map<String, Object> handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<>();

        Content content = tryGetFromRequest(request);
        Form form = null;

        if (content != null) {
            form = new AksessContentForm(content);
        }

        if (form != null) {
            if (request.getMethod().equalsIgnoreCase("POST") && request.getParameter("isAksessFormSubmit") != null) {
                Map<String, String[]> values = new LinkedHashMap<>(request.getParameterMap());
                addValues(values, request);

                FormSubmission formSubmission = formSubmissionBuilder.buildFormSubmission(values, form, true);

                // Validate formsubmission
                List<FormError> errors = formSubmissionValidator.validate(formSubmission);
                if (errors != null && errors.size() > 0) {
                    // errrors
                    model.put("hasErrors", Boolean.TRUE);
                    model.put("formSubmission", formSubmission);
                    model.put("formErrors", errors);
                    form = filledFormBuilder.buildFilledForm(formSubmission, errors);
                } else {
                    addUserInformation(formSubmission, request);

                    for (OpenAksessPlugin plugin : pluginManager.getPlugins()) {
                        for (FormDeliveryService service : plugin.getFormDeliveryServices()) {
                            if (formDeliveryServiceIds.contains(service.getId())) {
                                service.deliverForm(formSubmission);
                            }
                        }
                    }
                    request.getSession(true).setAttribute("aksessFormSubmission", formSubmission);

                    model.put("formSubmission", formSubmission);
                    model.put("hasSubmitted", Boolean.TRUE);
                    model.put("hasErrors",Boolean.FALSE);

                    if (configuration.getBoolean("formengine.mailconfirmation.enabled", false)) {
                        sendConfirmationEmail(formSubmission, content);
                        model.put("mailSent", Boolean.TRUE);
                    }
                }
            } else {
                // Form is entered for the first time
                Map<String, String[]> prefillValues = new HashMap<>();
                prefill(prefillValues, request);
                if (prefillValues.size() > 0) {
                    FormSubmission formSubmission = formSubmissionBuilder.buildFormSubmission(prefillValues, form, true);
                    if (formSubmission.getValues() != null) {
                        form = filledFormBuilder.buildFilledForm(formSubmission, new ArrayList<FormError>());
                    }
                }
            }

        }

        model.put("form", form);

        return model;
    }

    protected void sendConfirmationEmail(FormSubmission formsubmission, Content currentPage) throws ConfigurationException {
        String from = configuration.getString("mail.from");
        String recipient = formsubmission.getSubmittedByEmail();
        if (recipient != null && recipient.contains("@")) {
            String subject = String.format(mailConfirmationSubject, formsubmission.getForm().getTitle());
            Map<String, Object> params = new HashMap<>();
            params.put("currentPage", currentPage);
            params.put("form", formsubmission);

            try {
                MailSender.send(from, recipient, subject, mailConfirmationTemplate, params);
            } catch (SystemException | ConfigurationException e) {
                log.error("", e);
            }
        }
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

    public void setFormDeliveryServiceIds(String formDeliveryServiceIds) {
        this.formDeliveryServiceIds = formDeliveryServiceIds;
    }

    public void setPluginManager(PluginManager<OpenAksessPlugin> pluginManager) {
        this.pluginManager = pluginManager;
    }

    public void setFormSubmissionValidator(FormSubmissionValidator formSubmissionValidator) {
        this.formSubmissionValidator = formSubmissionValidator;
    }

    public void setMailConfirmationSubject(String mailConfirmationSubject) {
        this.mailConfirmationSubject = mailConfirmationSubject;
    }

    public void setMailConfirmationTemplate(String mailConfirmationTemplate) {
        this.mailConfirmationTemplate = mailConfirmationTemplate;
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
            formSubmission.setAuthenticatedIdentity(session.getIdentity());
            if (formSubmission.getSubmittedByName() == null) {
                formSubmission.setSubmittedByName(user.getName());
            }
            if (formSubmission.getSubmittedByEmail() == null) {
                formSubmission.setSubmittedByEmail(user.getEmail());
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
     * Adds additional values to or overrides values in the form,
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

    public void setConfiguration(SystemConfiguration configuration) {
        this.configuration = configuration;
    }
}
