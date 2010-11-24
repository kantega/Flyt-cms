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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Main controller for the Form admin.
 */
@Controller
public class FormadminController {


    /**
     * Renders the form admin view.
     * Subsequent views below this, e.g. form submission lists are loaded with ajax.
     * 
     * @return Complete admin view with menu, navigator and content.
     */
    @RequestMapping("/")
    public String viewFormAdmin() {
        return "formadmin";
    }
}
