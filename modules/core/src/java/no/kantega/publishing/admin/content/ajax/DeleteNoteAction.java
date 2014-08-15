/*
 * Copyright 2010 Kantega AS
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

package no.kantega.publishing.admin.content.ajax;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.admin.AdminRequestParameters;
import no.kantega.publishing.admin.viewcontroller.SimpleAdminController;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.ao.NotesDao;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.content.api.ContentAO;
import no.kantega.publishing.content.api.ContentIdHelper;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.enums.Privilege;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteNoteAction  extends SimpleAdminController {
    @Autowired
    NotesDao notesDao;

    @Autowired
    private ContentAO contentAO;

    @Autowired
    private ContentIdHelper contentIdHelper;

    @Override
    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters params = new RequestParameters(request);


        String url = params.getString(AdminRequestParameters.ITEM_IDENTIFIER);

        int noteId = params.getInt("noteId");

        if (!"".equals(url)) {
            try {
                ContentIdentifier cid = contentIdHelper.fromRequestAndUrl(request, url);

                ContentManagementService cms = new ContentManagementService(request);

                Content content = cms.getContent(cid);
                SecuritySession securitySession = cms.getSecuritySession();
                if (securitySession.isAuthorized(content, Privilege.UPDATE_CONTENT)) {
                    if (noteId != -1) {
                        notesDao.removeNote(noteId);
                        int contentId = cid.getContentId();
                        int count = notesDao.getNotesByContentId(contentId).size();
                        contentAO.setNumberOfNotes(contentId, count);
                    }
                }
            } catch (ContentNotFoundException e) {
                // Do nothing
            }
        }

        return new ModelAndView(new RedirectView("ListNotes.action"));
    }
}
