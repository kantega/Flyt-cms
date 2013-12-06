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
import no.kantega.publishing.admin.content.util.EditContentHelper;
import no.kantega.publishing.admin.content.util.SaveContentHelper;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.enums.AttributeDataType;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import no.kantega.publishing.common.service.ContentManagementService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class SaveContentAction extends AbstractSaveContentAction {
    private String view;

    public ValidationErrors saveRequestParameters(Content content, RequestParameters param, ContentManagementService aksessService) throws SystemException, InvalidFileException, InvalidTemplateException{
        HttpServletRequest request = param.getRequest();
        ValidationErrors   errors  = new ValidationErrors();

        // Save all page attributes
        SaveContentHelper helper = new SaveContentHelper(request, content, AttributeDataType.CONTENT_DATA);

        return helper.getHttpParameters(errors);
    }

    public String getView() {
        return view;
    }

    protected void addRepeaterRow(Content content, String addRepeaterRow) {
        try {
            EditContentHelper.addRepeaterRow(content, addRepeaterRow, AttributeDataType.CONTENT_DATA);
        } catch (InvalidTemplateException e) {
            throw new SystemException("Failed adding repeater rows", e);
        }
    }


    protected void deleteRepeaterRow(Content content, String addRepeaterRow) {
        EditContentHelper.deleteRepeaterRow(content, addRepeaterRow, AttributeDataType.CONTENT_DATA);
    }


    Map<String, Object> getModel(Content content, HttpServletRequest request) {
        return new HashMap<>();
    }

    public void setView(String view) {
        this.view = view;
    }
}
