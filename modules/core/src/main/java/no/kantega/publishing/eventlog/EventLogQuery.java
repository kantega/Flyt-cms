package no.kantega.publishing.eventlog;

import no.kantega.publishing.common.data.enums.ObjectType;

import java.util.Date;

/**
 * Query object to search the event log.
 * from - Get events after this date
 * end - Get events before this date
 * userId - Get events performed by the user with this userId
 * subjectName - The name of the object that the event has been performed on.
 * eventName - The name of the event type which has been performed on the subject.
 */
public class EventLogQuery {
    private Date from;
    private Date to;
    private String userId;
    private String subjectName;
    private int subjectType = ObjectType.CONTENT;
    private String eventName;

    public Date getFrom() {
        return from;
    }

    public EventLogQuery setFrom(Date from) {
        this.from = from;
        return this;
    }

    public Date getTo() {
        return to;
    }

    public EventLogQuery setTo(Date to) {
        this.to = to;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public EventLogQuery setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public EventLogQuery setSubjectName(String subjectName) {
        this.subjectName = subjectName;
        return this;
    }

    public int getSubjectType() {
        return subjectType;
    }

    public EventLogQuery setSubjectType(int subjectType) {
        this.subjectType = subjectType;
        return this;
    }

    public String getEventName() {
        return eventName;
    }

    public EventLogQuery setEventName(String eventName) {
        this.eventName = eventName;
        return this;
    }
}
