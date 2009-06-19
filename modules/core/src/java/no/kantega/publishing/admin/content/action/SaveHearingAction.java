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

import no.kantega.publishing.common.data.Hearing;
import no.kantega.publishing.common.data.HearingInvitee;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.HearingAO;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.exception.SystemException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import org.apache.log4j.Logger;


public class SaveHearingAction extends HttpServlet  {

    private Logger log = Logger.getLogger(getClass());
    public static final String HEARING_KEY = SaveHearingAction.class.getName() + ".HearingKey";
    public static final String HEARING_INVITEES_KEY = SaveHearingAction.class.getName() +".HearingInviteeKey";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Hearing hearing = getHearing();
        List invitees = new ArrayList();

        Map map = new HashMap();
        ValidationErrors errors = bind(hearing, invitees, request, map);

        if(errors.getLength() == 0) {
            validate(hearing, invitees, errors, request);
        }
        if(errors.getLength() > 0) {
            Iterator i = map.keySet().iterator();
            while (i.hasNext()) {
                String key = (String) i.next();
                String value = (String)map.get(key);
                request.setAttribute(key, value);
            }
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("hearing_body.jsp").forward(request, response);
        } else {
            try {
                saveHearing(hearing, invitees, request);
            } catch (SystemException e) {
                throw new ServletException(e);
            }
            request.getRequestDispatcher("hearing_saved.jsp").forward(request, response);
        }



    }

    private void saveHearing(Hearing hearing, List invitees, HttpServletRequest request) throws SystemException {
        request.getSession().setAttribute(HEARING_KEY, hearing);
        request.getSession().setAttribute(HEARING_INVITEES_KEY, invitees);
    }

    private void validate(Hearing hearing, List invitees, ValidationErrors errors, HttpServletRequest request) {
        if(hearing.getDeadLine().getTime() < new Date().getTime()) {
            errors.add("deadline", "Høringsfrist må være i fram i tid");
        }
        if(invitees.size() == 0) {
            errors.add("orgunits", "Høringsinstanser er ikke angitt");
        }

        Content content = (Content)request.getSession().getAttribute("currentContent");
        if(content.getChangeDescription() == null || content.getChangeDescription().trim().length() == 0) {
            errors.add("description", "Endringsbeskrivelse må være fylt ut");
        }

    }

    private ValidationErrors bind(Hearing hearing, List invitees, HttpServletRequest request, Map map) {

        ValidationErrors errors = new ValidationErrors();

        RequestParameters param = new RequestParameters(request);

        String deadlineString = param.getString("deadline");
        if(deadlineString == null || deadlineString.equals("")) {
            errors.add("deadline", "Høringsfrist må oppgis");
        } else {
            try {
                Date date = new SimpleDateFormat(Aksess.getDefaultDateFormat()).parse(deadlineString);
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(date);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                hearing.setDeadLine(calendar.getTime());
            } catch (ParseException e) {
                errors.add("deadline", "Høringsfrist har ikke gyldig format (" +Aksess.getDefaultDateFormat() +")");
            }
        }
        map.put("deadline", deadlineString);

        String description  = param.getString("description");

        if(description == null) {
            errors.add("description", "Endringsbeskrivelse må være fylt ut");
        }
        else {
             Content content = (Content)request.getSession().getAttribute("currentContent");
            content.setChangeDescription(description);
        }
        map.put("description", description);

        String[] orgunits = param.getString("orgunits").split(",");
        map.put("orgunits", request.getParameter("orgunits"));

        for (int i = 0; i < orgunits.length; i++) {
            String orgunit = orgunits[i];
            if(!orgunit.equals("")) {
                HearingInvitee invitee = new HearingInvitee();
                invitee.setType(HearingInvitee.TYPE_ORGUNIT);
                invitee.setReference(orgunit);
                invitees.add(invitee);
            }
        }

        String[]  users = param.getString("users").split(",");
        map.put("users", param.getString("users"));
        for (int i = 0; i < users.length; i++) {
            String user = users[i];
            if(!user.equals("")) {
                HearingInvitee invitee = new HearingInvitee();
                invitee.setType(HearingInvitee.TYPE_PERSON);
                invitee.setReference(user);
                invitees.add(invitee);
            }
        }

        return errors;
    }

    private Hearing getHearing() {
        return new Hearing();
    }
}
