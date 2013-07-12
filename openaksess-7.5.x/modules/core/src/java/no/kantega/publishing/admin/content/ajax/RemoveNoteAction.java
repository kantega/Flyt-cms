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

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

import no.kantega.publishing.common.ao.NotesDao;
import no.kantega.publishing.admin.viewcontroller.SimpleAdminController;
import no.kantega.commons.client.util.RequestParameters;

import java.util.HashMap;
import java.util.Map;

public class RemoveNoteAction extends SimpleAdminController {
    @Autowired
    NotesDao notesDao;

    @Override
    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestParameters param = new RequestParameters(request);

        int noteId = param.getInt("noteId");
        if (noteId != -1) {
            notesDao.removeNote(noteId);
        }

        return new ModelAndView(new RedirectView("ListNotes.action"), null);
    }
}

