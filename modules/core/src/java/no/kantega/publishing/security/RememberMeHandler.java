package no.kantega.publishing.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RememberMeHandler {

    public HttpServletResponse setRememberMe(HttpServletResponse response, String username, String domain) throws Exception;
    public String[] hasRememberMe(HttpServletRequest request) throws Exception;

}
