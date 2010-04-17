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
import no.kantega.publishing.common.data.Note;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.exception.InvalidFileException;
import no.kantega.commons.exception.RegExpSyntaxException;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

public class SaveNotesAction extends AbstractSaveContentAction {
    private static String SOURCE = "aksess.SaveNotesAction";

    private String view;

    public ValidationErrors saveRequestParameters(Content content, RequestParameters param, ContentManagementService aksessService) throws SystemException, InvalidFileException, InvalidTemplateException, RegExpSyntaxException {
        HttpServletRequest request = param.getRequest();

        String note = param.getString("note");
        String noteaction = param.getString("noteaction");

        /*
        if("addnote".equals(noteaction)) {
            SecuritySession ss = SecuritySession.getInstance(request);
            if(note != null && note.length() > 0 && content.getId() != -1) {
                Note n = new Note();
                n.setAuthor(ss.getUser().getName());
                n.setDate(new Date());
                n.setText(note);
                n.setContentId(content.getId());
                NotesAO.addNote(n);
                int count = NotesAO.getNotesByContentId(content.getId()).length;
                ContentAO.setNumberOfNotes(content.getId(), count);
                content.setNumberOfNotes(count);
            }
        } else if("removenote".equals(noteaction)) {
            int nid = param.getInt("noteid");
            NotesAO.removeNote(nid);
            int count = NotesAO.getNotesByContentId(content.getId()).length;
            ContentAO.setNumberOfNotes(content.getId(), count);
            content.setNumberOfNotes(count);
        }*/

        return new ValidationErrors();
    }

    public String getView() {
        return view;
    }

    Map<String, Object> getModel(Content content, HttpServletRequest request) {
        return new HashMap<String, Object>();
    }

    public void setView(String view) {
        this.view = view;
    }
}