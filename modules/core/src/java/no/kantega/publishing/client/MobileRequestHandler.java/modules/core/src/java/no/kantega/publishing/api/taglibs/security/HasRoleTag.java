package no.kantega.publishing.api.taglibs.security;

import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.security.SecuritySession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

public class HasRoleTag  extends ConditionalTagSupport {
    private static final String SOURCE = "aksess.HasRoleTag";

    private String[] roles;

    protected boolean condition()  {
        HttpServletRequest request  = (HttpServletRequest)pageContext.getRequest();
        SecuritySession session = null;
        try {
            session = SecuritySession.getInstance(request);
            if (session.isLoggedIn() && roles != null) {
                if (session.isUserInRole(roles)) {
                    return true;
                }
            }
        } catch (SystemException e) {
            Log.error(SOURCE, e, null, null);
        }

        return false;
    }

    public int doAfterBody() throws JspException {
        roles = null;
        return SKIP_BODY;
    }

    public void setRoles(String role) {
        if (role != null) {
            this.roles = role.split(",");
        }        
    }
}

