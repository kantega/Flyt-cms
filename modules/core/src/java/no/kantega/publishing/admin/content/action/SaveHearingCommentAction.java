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

import no.kantega.publishing.common.data.*;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.HearingAO;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.exception.SystemException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import org.apache.log4j.Logger;


public class SaveHearingCommentAction extends HttpServlet  {

    private Logger log = Logger.getLogger(getClass());
    public static final String HEARING_KEY = SaveHearingCommentAction.class.getName() + ".HearingKey";
    public static final String HEARING_INVITEES_KEY = SaveHearingCommentAction.class.getName() +".HearingInviteeKey";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestParameters param = new RequestParameters(request);

        try {
            String sourceurl = param.getString("sourceurl");

            String username = SecuritySession.getInstance(request).getUser().getId();

            String comment = param.getString("comment");

            int hearingID = param.getInt("hearingId");

            // TODO: Check if is hearing instance

            if(comment != null && !comment.trim().equals("")) {
                HearingComment hc = new HearingComment();
                hc.setHearingId(hearingID);
                hc.setComment(comment);
                hc.setDate(new Date());
                hc.setUserRef(username);
                HearingAO.saveOrUpdate(hc);
            }
            response.sendRedirect(sourceurl);
        } catch (SystemException e) {
            throw new ServletException(e);
        }

    }

}
