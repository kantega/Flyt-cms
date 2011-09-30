package org.kantega.openaksess.plugins.failedSubmissions;

import no.kantega.commons.exception.ConfigurationException;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.ao.EventLogAO;
import no.kantega.publishing.common.data.EventLogEntry;
import no.kantega.publishing.common.data.enums.Event;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Calendar;
import java.util.List;

@Controller
public class FailedSubmissionsController {

    private EventLogAO eventLogAO;

    @RequestMapping(method = RequestMethod.GET)
    public String getFailedEmailSubmissionEvents(@RequestParam(defaultValue = Event.FAILED_FORM_SUBMISSION, required = false) String eventName, Model model) throws Exception {
        Calendar from = getFromDate();
        List<EventLogEntry> list = eventLogAO.createQuery().setEventName(eventName).setFrom(from.getTime()).list();
        model.addAttribute("failedSubmissions", list);
        return "org/kantega/openaksess/plugins/failedSubmissions/view";
    }

    private Calendar getFromDate() throws ConfigurationException {
        Calendar from = Calendar.getInstance();
        int numberOfMinutesToShow = Aksess.getConfiguration().getInt("failedsubmissions.interval", 15);
        from.add(Calendar.MINUTE, -numberOfMinutesToShow);
        return from;
    }

    @Required
    public void setEventLogAO(EventLogAO eventLogAO) {
        this.eventLogAO = eventLogAO;
    }
}
