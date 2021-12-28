package no.kantega.publishing.eventlog;

import no.kantega.publishing.api.model.BaseObject;
import no.kantega.publishing.security.SecuritySession;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventLog {

    List<EventLogEntry> getQueryResult(EventLogQuery eventLogQuery);

    void log(SecuritySession securitySession, HttpServletRequest request, String event, String subject, BaseObject object);

    void log(SecuritySession securitySession, HttpServletRequest request, String event, String subject);

    void log(String username, String remoteAddr, String event, String subject, BaseObject object);
}
