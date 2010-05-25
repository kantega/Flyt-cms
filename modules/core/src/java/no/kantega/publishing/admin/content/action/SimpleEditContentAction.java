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

import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.exception.InvalidFileException;
import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import no.kantega.publishing.common.exception.ObjectLockedException;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.exception.InvalidParameterException;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.util.RequestHelper;
import no.kantega.publishing.admin.content.util.EditContentHelper;
import no.kantega.publishing.security.SecuritySession;

import java.util.List;

public class SimpleEditContentAction extends AbstractSimpleEditContentAction {
    protected SecuritySession getSecuritySession(HttpServletRequest request) {
        return SecuritySession.getInstance(request);
    }

    protected Content getContentForEdit(HttpServletRequest request) throws InvalidFileException, ObjectLockedException, NotAuthorizedException, InvalidTemplateException {
        RequestParameters param = new RequestParameters(request);

        ContentManagementService cms = new ContentManagementService(request);

        int thisId = param.getInt("thisId");
        int parentId = param.getInt("parentId");

        Content content = null;
        ContentIdentifier cid = new ContentIdentifier();

        if (thisId != -1) {
            // Edit existing page
            cid.setAssociationId(thisId);
            content = cms.getContent(cid, false);
        } else if (parentId != -1) {
            // Create new page
            ContentCreateParameters createParam = new ContentCreateParameters(request);
            content = cms.createNewContent(createParam);
        } else {
            throw new InvalidParameterException("", "");
        }

        return content;
    }
}
