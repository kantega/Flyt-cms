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
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.admin.content.util.SaveHearingHelper;
import no.kantega.publishing.security.interceptors.AdminViewInterceptor;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.Hearing;
import no.kantega.publishing.common.data.HearingInvitee;
import no.kantega.publishing.security.SecuritySession;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


public class SaveHearingAction extends AbstractController {
	private String confirmView;
	private String formView;

	public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Content content = (Content) request.getSession(true).getAttribute(AdminSessionAttributes.CURRENT_EDIT_CONTENT);

		setDeadlineIfNotPresent(content.getHearing());
		addCurrentUserAsInvitee(request, content.getHearing());

		Map<String, Object> model = new HashMap<String, Object>();

		if (request.getMethod().equalsIgnoreCase("POST")) {

			SaveHearingHelper helper = new SaveHearingHelper(request, content);
			ValidationErrors errors = new ValidationErrors();
			helper.getHttpParameters(errors);
			if (errors.getLength() == 0) {
				// Save

				return new ModelAndView(confirmView);
			}
			model.put("errors", errors);
		}

		return new ModelAndView(formView, model);
	}

	private void setDeadlineIfNotPresent(Hearing hearing) {
		Date deadline = hearing.getDeadLine();
		if (deadline == null) {
			//Set deadline a week from now.
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DAY_OF_MONTH, 7);
			hearing.setDeadLine(cal.getTime());
		}
	}

	public void addCurrentUserAsInvitee(HttpServletRequest request, Hearing hearing) {
		String user = SecuritySession.getInstance(request).getUser().getId();
		HearingInvitee invitee = new HearingInvitee();
		invitee.setType(HearingInvitee.TYPE_PERSON);
		invitee.setReference(user);
		if (!contains(hearing.getInvitees(), invitee)) {
			hearing.getInvitees().add(invitee);
		}
	}

	private boolean contains(List<HearingInvitee> invitees, HearingInvitee invitee) {
		for (HearingInvitee check : invitees) {
			if (check.getReference().equals(invitee.getReference()))
				return true;
		}
		return false;
	}

	public void setConfirmView(String confirmView) {
		this.confirmView = confirmView;
	}

	public void setFormView(String formView) {
		this.formView = formView;
	}
}
