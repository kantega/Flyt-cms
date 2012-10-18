package org.kantega.openaksess.plugins.failedSubmissions;

import no.kantega.commons.exception.ConfigurationException;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.eventlog.Event;
import no.kantega.publishing.eventlog.EventLog;
import no.kantega.publishing.eventlog.EventLogEntry;
import no.kantega.publishing.eventlog.EventLogQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Calendar;
import java.util.List;

@Controller
public class FailedSubmissionsController {

    @Autowired
    private EventLog eventLog;

    @RequestMapping(method = RequestMethod.GET)
    public String getFailedEmailSubmissionEvents(@RequestParam(defaultValue = Event.FAILED_FORM_SUBMISSION, required = false) String eventName, Model model) throws Exception {
        Calendar from = getFromDate();
        EventLogQuery eventLogQuery = new EventLogQuery(from.getTime(), null, null, null, null);
        List<EventLogEntry> list = eventLog.getQueryResult(eventLogQuery);
        model.addAttribute("failedSubmissions", list);
        return "org/kantega/openaksess/plugins/failedSubmissions/view";
    }

    private Calendar getFromDate() throws ConfigurationException {
        Calendar from = Calendar.getInstance();
        int numberOfMinutesToShow = Aksess.getConfiguration().getInt("failedsubmissions.interval", 15);
        from.add(Calendar.MINUTE, -numberOfMinutesToShow);
        return from;
    }

}
