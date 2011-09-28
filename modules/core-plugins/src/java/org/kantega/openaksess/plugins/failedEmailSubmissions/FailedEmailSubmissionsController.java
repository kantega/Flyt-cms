package org.kantega.openaksess.plugins.failedEmailSubmissions;

import no.kantega.publishing.common.ao.EventLogAO;
import no.kantega.publishing.common.data.EventLogEntry;
import no.kantega.publishing.common.data.enums.Event;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class FailedEmailSubmissionsController {

    private EventLogAO eventLogAO;

    @RequestMapping(method = RequestMethod.GET)
    public String getFailedEmailSubmissionEvents(Model model) throws Exception {
        List<EventLogEntry> list = eventLogAO.createQuery().setEventName(Event.FAILED_EMAIL_SUBMISSION).list();
        model.addAttribute("failedEmailSubmissions", list);
        return "org/kantega/openaksess/plugins/failedEmailSubmissions/view";
    }

    @Required
    public void setEventLogAO(EventLogAO eventLogAO) {
        this.eventLogAO = eventLogAO;
    }
}
