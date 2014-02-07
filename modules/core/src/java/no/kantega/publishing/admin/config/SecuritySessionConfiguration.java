package no.kantega.publishing.admin.config;

import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.Role;
import no.kantega.publishing.security.data.User;
import no.kantega.publishing.security.realm.SecurityRealm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Configuration
public class SecuritySessionConfiguration {

    private SecurityRealm securityRealm;

    @Bean
    @Scope("request")
    public SecuritySession securitySession(HttpServletRequest request){
        SecuritySession unauthenticatedInstance = createNewSecuritySession();
        SecuritySession securitySession;
        HttpSession session = request.getSession(false);
        if(session == null){
            securitySession = unauthenticatedInstance;
        } else {
            securitySession = (SecuritySession) session.getAttribute("aksess.securitySession");
            if (securitySession == null){
                securitySession = unauthenticatedInstance;
            }
        }
        return securitySession;
    }

    private SecuritySession createNewSecuritySession() {
        return new SecuritySession(securityRealm);
    }

    public void setSecurityRealm(SecurityRealm securityRealm) {
        this.securityRealm = securityRealm;
    }
}
