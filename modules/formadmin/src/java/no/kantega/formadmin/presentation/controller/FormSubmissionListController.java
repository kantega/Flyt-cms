/*
 * Copyright 2010 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.formadmin.presentation.controller;

import no.kantega.formengine.administration.FormAdministration;
import no.kantega.formengine.model.*;
import no.kantega.formengine.state.DefaultStateIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Generates views on form instance level, e.g. a list of all form submissions of a certain instance.
 */
@Controller
public class FormSubmissionListController extends FormAdminBaseController {

    private FormAdministration formAdministration;

    @Autowired
    public FormSubmissionListController(FormAdministration formAdministration) {
        this.formAdministration = formAdministration;
    }

    @RequestMapping("/forminstance")
    public String getFormSubmissionList(@RequestParam("instanceId") Integer instanceId, @RequestParam(value = "stateId", required = false) String stateId, HttpServletRequest request, Model model) {
        FormSubmissionQuery query = formAdministration.createFormSubmissionQuery();
        FormTypeInstanceIdentifier instanceIdentifier = new FormTypeInstanceIdentifier();
        instanceIdentifier.setId(instanceId);
        query.setFormTypeInstance(instanceIdentifier);
        if (stateId != null) {
            DefaultStateIdentifier stateIdentifier = new DefaultStateIdentifier();
            stateIdentifier.setId(stateId);
            query.setState(stateIdentifier);
        }

        List<FormSubmission> formSubmissions = formAdministration.searchFormSubmissions(query, getIdentityFromRequest(request));
        model.addAttribute("formSubmissions", formSubmissions);

        FormTypeInstanceQuery typeInstanceQuery = formAdministration.createFormTypeInstanceQuery().setId(instanceId);
        List<FormTypeInstance> instances = formAdministration.searchFormTypeInstances(typeInstanceQuery, getIdentityFromRequest(request));
        if (instances == null || instances.size() != 1) {
            throw new RuntimeException("Attempted to view non-existing form type instance");
        }
        model.addAttribute("formTypeInstance", instances.get(0));
        
        return "formsubmissionlist";
    }
}
