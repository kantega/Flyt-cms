/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.admin.content.action;

import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.admin.content.util.SaveHearingHelper;
import no.kantega.publishing.admin.viewcontroller.AdminController;
import no.kantega.publishing.common.data.Hearing;
import no.kantega.publishing.common.data.HearingInvitee;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.HearingAO;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.exception.SystemException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;


public class SaveHearingAction extends AdminController {
    private String confirmView;
    private String formView;

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Content content = (Content)request.getSession(true).getAttribute(AdminSessionAttributes.CURRENT_EDIT_CONTENT);

        Map<String, Object> model = new HashMap<String, Object>();

        if (request.getMethod().equalsIgnoreCase("POST")) {
            SaveHearingHelper helper = new SaveHearingHelper(request, content);
            ValidationErrors errors = new ValidationErrors();
            helper.getHttpParameters(errors);
            if (errors.getLength() == 0) {
                // Save
                return new ModelAndView(confirmView);
            }
            model.put("errors", errors);
        }

        return new ModelAndView(formView, model);
    }

    public void setConfirmView(String confirmView) {
        this.confirmView = confirmView;
    }

    public void setFormView(String formView) {
        this.formView = formView;
    }
}
