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
import no.kantega.publishing.common.data.DisplayTemplate;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.admin.content.util.SaveContentHelper;
import no.kantega.publishing.admin.content.action.AbstractSaveContentAction;
import no.kantega.publishing.forum.ForumProvider;
import no.kantega.publishing.spring.RootContext;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.exception.InvalidFileException;
import no.kantega.commons.exception.RegExpSyntaxException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.HashMap;

public class SaveMetadataAction extends AbstractSaveContentAction {
    private String view;

    public ValidationErrors saveRequestParameters(Content content, RequestParameters param, ContentManagementService aksessService) throws SystemException, InvalidFileException, InvalidTemplateException, RegExpSyntaxException {
        HttpServletRequest request = param.getRequest();
        ValidationErrors   errors  = new ValidationErrors();

        // Hard coded metadata
        content.setAltTitle(param.getString("alttitle", 255));
        content.setKeywords(param.getString("keywords", 8000));
        content.setPublisher(param.getString("publisher", 64));
        content.setDocumentTypeId(param.getInt("documenttype"));
        content.setDocumentTypeIdForChildren(param.getInt("documenttypeidforchildren"));
        content.setOwner(param.getString("owner", 128));
        content.setOwnerPerson(param.getString("ownerperson", 128));
        content.setLanguage(param.getInt("language"));
        content.setForumId(param.getInt("forumid"));

        // Dynamic metadata
        SaveContentHelper helper = new SaveContentHelper(request, content, AttributeDataType.META_DATA);

        return helper.getHttpParameters(errors);

    }

    public String getView() {
        return view;
    }

    Map<String, Object> getModel(Content content, HttpServletRequest request) {
        Map<String, Object> model =  new HashMap<String, Object>();

        ContentManagementService cms = new ContentManagementService(request);

        Map forumProviders = RootContext.getInstance().getBeansOfType(ForumProvider.class);
        if (content.getDisplayTemplateId() > 0) {
            DisplayTemplate dt = cms.getDisplayTemplate(content.getDisplayTemplateId());
            if (forumProviders.size() > 0 && dt.getDefaultForumId() != null) {
                ForumProvider forumProvider = (ForumProvider) forumProviders.values().iterator().next();
                model.put("forumProvider", forumProvider);
            }
        }

        model.put("documentTypes", cms.getDocumentTypes());
        
        return model;
    }

    public void setView(String view) {
        this.view = view;
    }
}