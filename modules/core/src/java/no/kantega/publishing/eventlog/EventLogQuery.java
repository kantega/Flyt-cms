package no.kantega.publishing.eventlog;

import no.kantega.publishing.common.data.enums.ObjectType;

import java.util.Date;

/**
 * Søk i eventlogg
 * from - Dato fra
 * end - Dato til
 * userId - Brukerid
 * subjectName - Navn på objekt i loggen (navn på side f.eks)
 * eventName - Hendelse
 */
public class EventLogQuery {
    private Date from;
    private Date to;
    private String userId;
    private String subjectName;
    private int subjectType = ObjectType.CONTENT;
    private String eventName;

    /**
     *
     * @param from date
     * @param to date
     * @param userId of user performing logged action
     * @param subjectName Name of the object performed action upon
     * @param eventName the name of the type of event
     */
    public EventLogQuery(Date from, Date to, String userId, String subjectName, String eventName) {
        this.from = from;
        this.to = to;
        this.userId = userId;
        this.subjectName = subjectName;
        this.eventName = eventName;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public int getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(int subjectType) {
        this.subjectType = subjectType;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
