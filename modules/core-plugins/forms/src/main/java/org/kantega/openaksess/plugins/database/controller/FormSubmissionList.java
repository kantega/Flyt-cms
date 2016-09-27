package org.kantega.openaksess.plugins.database.controller;

import no.kantega.publishing.api.forms.model.FormSubmission;
import org.kantega.openaksess.plugins.database.dao.FormSubmissionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/administration/submittedForms")
public class FormSubmissionList {

    @Autowired
    private FormSubmissionDao dao;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView listFormData(HttpServletRequest request) throws Exception {
        Map<String, Object> model = new HashMap<>();
        List<FormSubmission> list = dao.getFormSubmissions()
            .stream()
            .sorted(
                Comparator.comparing(f -> f.getForm().getId())
            )
            .collect(Collectors.toList());
        model.put("fieldList", list);
        model.put("contextPath", request.getContextPath());
        return new ModelAndView("org/kantega/openaksess/plugins/forms/view", model);
    }



}
