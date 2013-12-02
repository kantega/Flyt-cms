package no.kantega.publishing.security.action;

import no.kantega.publishing.security.realm.SecurityRealm;
import no.kantega.publishing.security.realm.SecurityRealmFactory;
import no.kantega.publishing.spring.RootContext;
import no.kantega.security.api.identity.DefaultIdentity;
import no.kantega.security.api.identity.Identity;
import no.kantega.security.api.password.PasswordManager;
import no.kantega.security.api.password.ResetPasswordTokenManager;
import no.kantega.security.api.profile.ProfileManager;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.apache.http.util.TextUtils.isBlank;


public abstract class AbstractLoginAction implements Controller {
    private String loginLayout;

    protected PasswordManager getPasswordManager(String domain) {
        PasswordManager passwordManager = null;
        ApplicationContext context = RootContext.getInstance();
        Map passwordManagers = context.getBeansOfType(PasswordManager.class);
        if (passwordManagers != null) {
            for (Object o : passwordManagers.values()) {
                passwordManager = (PasswordManager) o;
                if (passwordManager.getDomain().equalsIgnoreCase(domain)) {
                    break;
                }
            }
        }
        return passwordManager;
    }

    protected ResetPasswordTokenManager getResetPasswordTokenManager() {
        ResetPasswordTokenManager resetPasswordTokenManager = null;
        ApplicationContext context = RootContext.getInstance();
        Map resetPasswordTokenManagerers = context.getBeansOfType(ResetPasswordTokenManager.class);
        if (resetPasswordTokenManagerers != null) {
            for (Object o : resetPasswordTokenManagerers.values()) {
                resetPasswordTokenManager = (ResetPasswordTokenManager) o;
            }
        }
        return resetPasswordTokenManager;
    }

    protected ProfileManager getProfileManager() {
        SecurityRealm realm = SecurityRealmFactory.getInstance();
        return realm.getProfileManager();
    }

    protected Identity getIdentityFromRequest(HttpServletRequest request) {
        String userid = request.getParameter("username");
        String domain = request.getParameter("domain");
        if (isBlank(userid) || isBlank(domain)) {
            return null;
        }

        DefaultIdentity identity = new DefaultIdentity();
        identity.setDomain(domain);
        identity.setUserId(userid);
        return identity;
    }

    public String getLoginLayout() {
        return loginLayout;
    }

    public void setLoginLayout(String loginLayout) {
        this.loginLayout = loginLayout;
    }
}
