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

import no.kantega.formadmin.presentation.taglib.FormadminMapEntry;
import no.kantega.formadmin.presentation.util.FormTypeNavigatorMapper;
import no.kantega.formengine.administration.FormAdministration;
import no.kantega.formengine.model.FormTypeInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Provides model data for the navigator.
 */
@Controller
public class NavigatorController extends FormAdminBaseController {

    private FormAdministration formAdministration;
    private FormTypeNavigatorMapper navigatorMapper;

    @Autowired
    public NavigatorController(FormAdministration formAdministration, FormTypeNavigatorMapper navigatorMapper) {
        this.formAdministration = formAdministration;
        this.navigatorMapper = navigatorMapper;
    }

    /**
     * Sets up the navigator
     *
     * @param currentInstance
     * @param currentState
     * @param openInstances
     * @param model
     * @param request
     * @return Navigator view
     */
    @RequestMapping("/navigator")
    public String getNavigator(@RequestParam(value = "currentInstance", required = false) Integer currentInstance, @RequestParam(value = "currentState", required = false) String currentState, @RequestParam(value = "openInstances", required = false) Integer[] openInstances, Model model, HttpServletRequest request) {

        List<FormTypeInstance> instances = formAdministration.searchFormTypeInstances(formAdministration.createFormTypeInstanceQuery(), getIdentityFromRequest(request));

        int currInstanceId = (currentInstance != null)? currentInstance : 0; //Explicit unboxing in case currentInstance is null.
        FormadminMapEntry navigatorContent = navigatorMapper.mapInstancesToNavigatorMapEntries(instances, currInstanceId, currentState, unbox(openInstances));

        model.addAttribute("navigatorContent", navigatorContent);
        model.addAttribute("currentInstance", currentInstance);
        model.addAttribute("currentState", currentState);
        model.addAttribute("openInstances", openInstances);

        return "navigator";
    }

    private int[] unbox(Integer[] integerArr) {
        if (integerArr == null) {
            return null;
        }
        int[] intArr = new int[integerArr.length];
        for (int i = 0, integerArrLength = integerArr.length; i < integerArrLength; i++) {
            intArr[i] = integerArr[i];
        }
        return intArr;
    }

}
