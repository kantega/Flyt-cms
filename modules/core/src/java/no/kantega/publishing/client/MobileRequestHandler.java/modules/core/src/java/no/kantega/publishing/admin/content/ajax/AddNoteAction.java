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

import no.kantega.publishing.common.ao.NotesDao;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.Note;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.admin.viewcontroller.SimpleAdminController;
import no.kantega.publishing.admin.AdminRequestParameters;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.commons.client.util.RequestParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

public class AddNoteAction extends SimpleAdminController {
    @Autowired
    NotesDao notesDao;

    @Override
    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters params = new RequestParameters(request);

        SecuritySession securitySession = SecuritySession.getInstance(request);

        String noteText = params.getString("note");

        String url = params.getString(AdminRequestParameters.ITEM_IDENTIFIER);

        // Extracting currently selected content from it's url
        Content currentContent = null;
        if (!"".equals(url)) {
            ContentIdentifier cid = null;
            try {
                cid = new ContentIdentifier(request, url);

                Note note = new Note();
                note.setText(noteText);
                note.setDate(new Date());
                note.setContentId(cid.getContentId());
                note.setAuthor(securitySession.getUser().getName());

                notesDao.addNote(note);
                int count = notesDao.getNotesByContentId(cid.getContentId()).size();
                ContentAO.setNumberOfNotes(cid.getContentId(), count);

            } catch (ContentNotFoundException e) {
                // Do nothing
            }
        }

        Map<String, Object> model = new HashMap<String, Object>();
        model.put(AdminRequestParameters.ITEM_IDENTIFIER, url);
        return new ModelAndView(new RedirectView("ListNotes.action"), model);
    }
}
