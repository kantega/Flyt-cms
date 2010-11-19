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
import no.kantega.publishing.security.SecuritySession;
import no.kantega.security.api.identity.Identity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class NavigatorController {

    private FormAdministration formAdministration;
    private FormTypeNavigatorMapper navigatorMapper;

    @Autowired
    public NavigatorController(FormAdministration formAdministration, FormTypeNavigatorMapper navigatorMapper) {
        this.formAdministration = formAdministration;
        this.navigatorMapper = navigatorMapper;
    }

    @RequestMapping("/navigator")
    public String getNavigator(Model model, HttpServletRequest request) {

        List<FormTypeInstance> instances = formAdministration.searchFormTypeInstances(formAdministration.createFormTypeInstanceQuery(), getIdentityFromRequest(request));

        FormadminMapEntry navigatorContent = navigatorMapper.mapInstancesToNavigatorMapEntries(instances);
        model.addAttribute("navigatorContent", navigatorContent);
        return "navigator";
    }



    //TODO: Refactor into separate class.
    private Identity getIdentityFromRequest(HttpServletRequest request) {
        SecuritySession securitySession = getSecuritySession(request);
        return securitySession.getIdentity();
    }

    /**
     * Abstraction in order to enable mocking of the SecuritySession.
     *
     * @param request
     * @return
     */
    protected SecuritySession getSecuritySession(HttpServletRequest request) {
        return SecuritySession.getInstance(request);

    }
}
