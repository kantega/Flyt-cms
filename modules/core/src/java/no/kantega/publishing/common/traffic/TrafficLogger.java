package no.kantega.publishing.common.traffic;

import no.kantega.publishing.common.data.Content;

import javax.servlet.http.HttpServletRequest;

public interface TrafficLogger {
    /**
     * Log access to the content
     * @param content - object to log
     * @param request - the request used to access the content
     */
    void log(Content content, HttpServletRequest request);
}
