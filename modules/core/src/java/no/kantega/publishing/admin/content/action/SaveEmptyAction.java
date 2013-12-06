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

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.commons.exception.InvalidFileException;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import no.kantega.publishing.common.service.ContentManagementService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


public class SaveEmptyAction extends AbstractSaveContentAction {
    private String view;

    public ValidationErrors saveRequestParameters(Content content, RequestParameters param, ContentManagementService aksessService) throws SystemException, InvalidFileException, InvalidTemplateException{
        // No data is editable on this page, return no errors
        ValidationErrors errors  = new ValidationErrors();

        return errors;
    }

    public String getView() {
        return view;
    }

    Map<String, Object> getModel(Content content, HttpServletRequest request) {
        return new HashMap<>();
    }

    public void setView(String view) {
        this.view = view;
    }
}
