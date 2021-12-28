/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.admin.ajax;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.api.content.ContentIdHelper;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.service.ContentManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Controller for maintaining the state of the user's currentContent session attribute,
 */
@Controller
@RequestMapping("/admin/publish/ContentState")
public class ContentStateHandler {
    private static final Logger log = LoggerFactory.getLogger(ContentStateHandler.class);

    @Autowired
    private ContentIdHelper contentIdHelper;
    /**
     * Updates the user's session with the currently viewed content.
     *
     * @param url Url of currently viewed page.
     */
    @RequestMapping(value = "/notifyContentUpdate.action", method = RequestMethod.POST)
    public ResponseEntity notifyContentUpdate(@RequestParam String url, HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session != null) {
            try {
                ContentManagementService cms = new ContentManagementService(request);
                ContentIdentifier cid = contentIdHelper.fromRequestAndUrl(request, url);
                Content current = cms.getContent(cid);
                session.setAttribute(AdminSessionAttributes.CURRENT_NAVIGATE_CONTENT, current);
            } catch (ContentNotFoundException e) {
                // Do nothing
            } catch (NotAuthorizedException e) {
                log.error("", e);
            }
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}
