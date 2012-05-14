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

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.common.ao.HearingAO;
import no.kantega.publishing.common.ao.NotesDao;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.HearingComment;
import no.kantega.publishing.common.data.Note;
import no.kantega.publishing.security.SecuritySession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;


public class SaveHearingCommentAction{

	private Logger log = Logger.getLogger(getClass());
	public static final String HEARING_KEY = SaveHearingCommentAction.class.getName() + ".HearingKey";
	public static final String HEARING_INVITEES_KEY = SaveHearingCommentAction.class.getName() + ".HearingInviteeKey";

	@Autowired
	private NotesDao notesDao;

	@RequestMapping("/aksess/hearing/SaveHearingComment.action")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestParameters param = new RequestParameters(request);

		try {
			String sourceurl = param.getString("sourceurl");

			String username = SecuritySession.getInstance(request).getUser().getId();

			String comment = param.getString("comment");

			int hearingID = param.getInt("hearingId");

			// TODO: Check if is hearing instance

			if (comment != null && !comment.trim().equals("")) {
				Content content = (Content) request.getSession(true).getAttribute(AdminSessionAttributes.CURRENT_NAVIGATE_CONTENT);
				String name = SecuritySession.getInstance(request).getUser().getName();

				HearingComment hc = new HearingComment();
				hc.setHearingId(hearingID);
				hc.setComment(comment);
				hc.setDate(new Date());
				hc.setUserRef(username);
				HearingAO.saveOrUpdate(hc);

				Note note = new Note();
				note.setText(comment);
				note.setDate(new Date());
				note.setContentId(content.getId());
				note.setAuthor(name);
				notesDao.addNote(note);
				int count = notesDao.getNotesByContentId(content.getId()).size();
				ContentAO.setNumberOfNotes(content.getId(), count);

			}
			response.sendRedirect(sourceurl);
		} catch (SystemException e) {
			throw new ServletException(e);
		}

	}

}
