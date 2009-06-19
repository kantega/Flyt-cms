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

import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.common.data.enums.AttributeDataType;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.admin.content.util.SaveContentHelper;
import no.kantega.publishing.admin.content.action.AbstractSaveContentAction;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.exception.InvalidFileException;
import no.kantega.commons.exception.RegExpSyntaxException;

import javax.servlet.http.HttpServletRequest;

public class SaveMetadataAction extends AbstractSaveContentAction {
    private static String SOURCE = "aksess.SaveMetadataAction";

    public ValidationErrors saveRequestParameters(Content content, RequestParameters param, ContentManagementService aksessService) throws SystemException, InvalidFileException, InvalidTemplateException, RegExpSyntaxException {
        HttpServletRequest request = param.getRequest();
        ValidationErrors   errors  = new ValidationErrors();

        // Faste metadata
        content.setAltTitle(param.getString("alttitle", 255));
        content.setKeywords(param.getString("keywords", 8000));
        content.setPublisher(param.getString("publisher", 64));
        content.setDocumentTypeId(param.getInt("documenttype"));
        content.setDocumentTypeIdForChildren(param.getInt("documenttypeforchildren"));
        content.setOwner(param.getString("owner", 128));
        content.setOwnerPerson(param.getString("ownerperson", 128));
        content.setLanguage(param.getInt("language"));
        content.setForumId(param.getInt("forumid"));

        SaveContentHelper helper = new SaveContentHelper(request, content, AttributeDataType.META_DATA);

        return helper.getHttpParameters(errors);

    }

    public String getEditPage() {
        return "editmetadata";
    }
}