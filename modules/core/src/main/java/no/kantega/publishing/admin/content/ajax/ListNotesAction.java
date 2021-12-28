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
import no.kantega.publishing.api.content.ContentIdHelper;
import no.kantega.publishing.api.content.ContentIdentifier;
import no.kantega.publishing.api.notes.NotesDao;
import no.kantega.publishing.common.data.Note;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class ListNotesAction extends SimpleAdminController {
    @Autowired
    NotesDao notesDao;

    @Autowired
    private ContentIdHelper contentIdHelper;

    @Override
    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<>();

        RequestParameters params = new RequestParameters(request);

        String url = params.getString(AdminRequestParameters.ITEM_IDENTIFIER);

        // Extracting currently selected content from it's url
        ContentIdentifier cid = null;
        if (!"".equals(url)) {
            try {
                cid = contentIdHelper.fromRequestAndUrl(request, url);

                List<Note> notes = notesDao.getNotesByContentId(cid.getContentId());

                model.put("notes", notes);
                model.put("addNotes", Boolean.TRUE);
            } catch (ContentNotFoundException e) {
                // Do nothing
            }
        }

        return new ModelAndView(getView(), model);
    }
}
