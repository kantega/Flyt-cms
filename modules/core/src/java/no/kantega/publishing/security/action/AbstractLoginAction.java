package no.kantega.publishing.security.action;

import com.google.common.collect.Maps;
import no.kantega.publishing.security.realm.SecurityRealm;
import no.kantega.publishing.security.realm.SecurityRealmFactory;
import no.kantega.security.api.identity.DefaultIdentity;
import no.kantega.security.api.identity.Identity;
import no.kantega.security.api.password.PasswordManager;
import no.kantega.security.api.password.ResetPasswordTokenManager;
import no.kantega.security.api.profile.ProfileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Collections.emptyMap;
import static org.apache.http.util.TextUtils.isBlank;


public abstract class AbstractLoginAction implements Controller {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private String loginLayout;

    private Map<String, PasswordManager> passwordManagers;
    private ResetPasswordTokenManager resetPasswordTokenManager;

    protected PasswordManager getPasswordManager(String domain) {
        Objects.requireNonNull(domain, "Domain must be non-null!");
        return passwordManagers.get(domain.toLowerCase());
    }

    protected ResetPasswordTokenManager getResetPasswordTokenManager() {
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

    @Autowired(required = false)
    public void setPasswordManagers(List<PasswordManager> passwordManagers) {
        log.info("Setting PasswordManagers: " + passwordManagers);
        if(passwordManagers == null){
            this.passwordManagers = emptyMap();
        } else {
            this.passwordManagers = Maps.newHashMapWithExpectedSize(passwordManagers.size());
            for (PasswordManager passwordManager : passwordManagers) {
                this.passwordManagers.put(passwordManager.getDomain().toLowerCase(), passwordManager);
            }
        }
    }

    @Autowired(required = false)
    public void setResetPasswordTokenManager(ResetPasswordTokenManager resetPasswordTokenManager) {
        log.info("Setting ResetPasswordTokenManager: " + resetPasswordTokenManager);
        this.resetPasswordTokenManager = resetPasswordTokenManager;
    }
}
