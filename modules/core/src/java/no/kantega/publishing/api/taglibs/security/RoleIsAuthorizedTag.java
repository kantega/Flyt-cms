package no.kantega.publishing.api.taglibs.security;

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.api.taglibs.content.util.AttributeTagHelper;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.security.data.Role;
import no.kantega.publishing.security.data.enums.Privilege;
import no.kantega.publishing.security.service.SecurityService;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

public class RoleIsAuthorizedTag extends ConditionalTagSupport {
    private static final String SOURCE = "aksess.HasRoleTag";

    private String role;
    private String collection;
    private Content contentObject;
    private int privilege = Privilege.VIEW_CONTENT;
    private boolean negate = false;
    private String contentId;

    protected boolean condition()  {
        try {
            if (contentObject == null) {
                contentObject = AttributeTagHelper.getContent(pageContext, collection, contentId);
            }
            if (role != null && contentObject != null) {
                Role r = new Role();
                r.setId(role);
                if (SecurityService.isAuthorized(r, contentObject, privilege)) {
                    return (!negate);
                }
            }
        } catch (SystemException e) {
            Log.error(SOURCE, e, null, null);
        } catch (NotAuthorizedException e) {
            // Do nothing
        }

        return negate;
    }

    public int doAfterBody() throws JspException {
        role = null;
        privilege = Privilege.VIEW_CONTENT;
        negate = false;
        return SKIP_BODY;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setPrivilege(int privilege) {
        this.privilege = privilege;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public void setObj(Content obj) {
        this.contentObject = obj;
    }

    public void setNegate(boolean negate) {
        this.negate = negate;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }
}


