package no.kantega.publishing.api.taglibs.security;

import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.security.SecuritySession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

public class HasRoleTag  extends ConditionalTagSupport {
    private static final Logger log = LoggerFactory.getLogger(HasRoleTag.class);
    private static WebApplicationContext webApplicationContext;

    private String[] roles;

    protected boolean condition()  {
        try {
            if (webApplicationContext == null) {
                webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(pageContext.getServletContext());
            }
            SecuritySession session = webApplicationContext.getBean(SecuritySession.class);
            if (session.isLoggedIn() && roles != null) {
                if (session.isUserInRole(roles)) {
                    return true;
                }
            }
        } catch (SystemException e) {
            log.error("", e);
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

