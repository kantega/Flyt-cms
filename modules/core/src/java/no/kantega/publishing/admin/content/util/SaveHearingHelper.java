package no.kantega.publishing.admin.content.util;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.commons.exception.RegExpSyntaxException;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.Hearing;
import no.kantega.publishing.common.data.HearingInvitee;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class SaveHearingHelper  {
    private static final String SOURCE = "aksess.admin.SaveHearingHelper";

    private HttpServletRequest request = null;
    private Content content = null;

    public SaveHearingHelper(HttpServletRequest request, Content content) throws SystemException {
        this.request = request;
        this.content = content;
    }


    public ValidationErrors getHttpParameters(ValidationErrors errors) throws RegExpSyntaxException {
        RequestParameters param = new RequestParameters(request, "utf-8");

        Hearing hearing = content.getHearing();

        try {
            Date hearingDeadline = param.getDate("attributeValue_hearing_deadline", Aksess.getDefaultDateFormat());
            hearing.setDeadLine(hearingDeadline);
        } catch(Exception e) {
            Map<String, Object> objects = new HashMap<String, Object>();
            objects.put("dateFormat", Aksess.getDefaultDateFormat());
            errors.add(null, "aksess.error.date", objects);
        }

        if(hearing.getDeadLine() == null) {
            errors.add("hearing_deadline", "aksess.feil.hearing.deadline.missing");
        } else if (hearing.getDeadLine().getTime() < new Date().getTime()) {
            errors.add("hearing_deadline", "aksess.feil.hearing.deadline.passed");
        }

        content.setChangeDescription(param.getString("attributeValue_hearing_changedescription"));
        if (content.getChangeDescription() == null || content.getChangeDescription().length() == 0) {
            errors.add("hearing_description", "aksess.feil.hearing.description.missing");
        }

        String[] orgunits = param.getString("attributeValue_hearing_orgunits").split(",");
        List<HearingInvitee> invitees = new ArrayList<HearingInvitee>();
        for (String orgunit : orgunits) {
            if (!orgunit.equals("")) {
                HearingInvitee invitee = new HearingInvitee();
                invitee.setType(HearingInvitee.TYPE_ORGUNIT);
                invitee.setReference(orgunit);
                invitees.add(invitee);
            }
        }

        String[]  users = param.getString("attributeValue_hearing_users").split(",");
        for (String user : users) {
            if (!user.equals("")) {
                HearingInvitee invitee = new HearingInvitee();
                invitee.setType(HearingInvitee.TYPE_PERSON);
                invitee.setReference(user);
                invitees.add(invitee);
            }
        }

        if (invitees.size() == 0) {
            errors.add(null, "aksess.feil.hearing.invitees.missing");
        }

        hearing.setInvitees(invitees);

        return errors;
    }
}
