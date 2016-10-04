package org.kantega.openaksess.plugins.database.controller;

import no.kantega.publishing.api.forms.model.DefaultForm;
import no.kantega.publishing.api.forms.model.Form;
import no.kantega.publishing.api.forms.model.FormSubmission;
import no.kantega.publishing.api.forms.model.FormValue;
import no.kantega.publishing.api.forms.service.FormService;
import org.kantega.openaksess.plugins.database.dao.FormSubmissionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;


@Controller
@RequestMapping("/administration/submittedForms")
public class FormSubmissionsController {

    @Autowired
    private FormSubmissionDao dao;

    @Autowired
    private FormService formService;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView listFormData(HttpServletRequest request, @RequestParam(required = false) Integer formId) throws Exception {
        Map<String, Object> model = getData(request, formId);
        String viewName = isNull(formId) ?
            "org/kantega/openaksess/plugins/forms/adminview"
            : "org/kantega/openaksess/plugins/forms/view";
        return new ModelAndView(viewName, model);
    }


    private Map<String, Object> getData(HttpServletRequest request, Integer formId) {
        Map<Integer, List<FormSubmission>> groupedByForm = dao.getFormSubmissions()
            .stream()
            .collect(Collectors.groupingBy(f -> f.getForm().getId()));

        List<Form> forms = groupedByForm.keySet()
            .stream()
            .filter(form -> isNull(formId) || Objects.equals(form, formId))
            .map(this::tryGetFormById)
            .collect(Collectors.toList());

        List<FormView> list = forms.stream()
            .map(f -> new FormView(f, groupedByForm.get(f.getId()), dao.getFieldNamesForForm(f.getId())))
            .collect(Collectors.toList());

        Map<String, Object> model = new HashMap<>();
        model.put("forms", list);
        model.put("contextPath", request.getContextPath());
        return model;
    }

    private Form tryGetFormById(Integer formId) {
        try {
            return formService.getFormById(formId);
        } catch (Exception e) {
            DefaultForm form = new DefaultForm();
            form.setId(formId);
            form.setTitle("Ukjent");
            return form;
        }
    }


    public static class FormView {
        private final Form form;
        private final List<FormSubmission> formSubmissions;
        private final List<String> fieldNamesForForm;

        FormView(Form form, List<FormSubmission> formSubmissions, List<String> fieldNamesForForm) {
            this.form = form;
            this.formSubmissions = formSubmissions;
            this.fieldNamesForForm = fieldNamesForForm;
        }

        public String getTitle() {
            return form.getTitle();
        }

        public String getPath() {
            return form.getUrl();
        }

        public List<FormSubmissionView> getSubmissions(){
            return formSubmissions.stream()
                .map(fs -> new FormSubmissionView(fs, fieldNamesForForm))
                .collect(Collectors.toList());
        }

        public List<String> getFields() {
            return fieldNamesForForm;
        }
    }

    public static class FormSubmissionView {
        private final FormSubmission submission;
        private final Map<String, String> values;
        private final List<String> fieldNamesForForm;

        public FormSubmissionView(FormSubmission submission, List<String> fieldNamesForForm) {
            this.submission = submission;
            values = submission.getValues()
                .stream()
                .collect(Collectors
                    .toMap(FormValue::getName, FormValue::getValuesAsString, (u,v) -> u + " " + v));
            this.fieldNamesForForm = fieldNamesForForm;
        }

        public String getSubmissionDate() {
            return new SimpleDateFormat("dd.MM.yyyy").format(submission.getSubmissionDate());
        }

        public List<String> getValues() {
            return fieldNamesForForm.stream().map(values::get).collect(Collectors.toList());
        }

        public int getId() {
            return submission.getFormSubmissionId();
        }
    }
}
