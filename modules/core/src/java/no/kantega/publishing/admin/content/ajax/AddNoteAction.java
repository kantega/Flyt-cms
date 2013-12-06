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

package no.kantega.publishing.admin.content.ajax;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.admin.AdminRequestParameters;
import no.kantega.publishing.admin.viewcontroller.SimpleAdminController;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.common.ao.NotesDao;
import no.kantega.publishing.common.data.Note;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.content.api.ContentAO;
import no.kantega.publishing.content.api.ContentIdHelper;
import no.kantega.publishing.security.SecuritySession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddNoteAction extends SimpleAdminController {
    @Autowired
    NotesDao notesDao;

    @Autowired
    private ContentAO contentAO;

    @Autowired
    private ContentIdHelper contentIdHelper;

    @Override
    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters params = new RequestParameters(request);

        SecuritySession securitySession = SecuritySession.getInstance(request);

        String noteText = params.getString("note");

        String url = params.getString(AdminRequestParameters.ITEM_IDENTIFIER);

        // Extracting currently selected content from it's url
        if (!"".equals(url)) {
            ContentIdentifier cid = null;
            try {
                cid = contentIdHelper.fromRequestAndUrl(request, url);
                int contentId = cid.getContentId();

                Note note = new Note();
                note.setText(noteText);
                note.setDate(new Date());
                note.setContentId(contentId);
                note.setAuthor(securitySession.getUser().getName());

                notesDao.addNote(note);
                int count = notesDao.getNotesByContentId(contentId).size();
                contentAO.setNumberOfNotes(contentId, count);

            } catch (ContentNotFoundException e) {
                // Do nothing
            }
        }

        Map<String, Object> model = new HashMap<>();
        model.put(AdminRequestParameters.ITEM_IDENTIFIER, url);
        return new ModelAndView(new RedirectView("ListNotes.action"), model);
    }
}
