package no.kantega.publishing.admin.config;

import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.realm.SecurityRealm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Configuration
public class SecuritySessionConfiguration {
    private final Logger log = LoggerFactory.getLogger(SecuritySessionConfiguration.class);

    private SecurityRealm securityRealm;

    @Bean
    @Scope("request")
    public SecuritySession securitySession(HttpServletRequest request){
        SecuritySession unauthenticatedInstance = createNewSecuritySession();
        SecuritySession securitySession;
        HttpSession session = request.getSession(false);
        if(session == null){
            log.debug("Returning unauthenticatedInstance");
            securitySession = unauthenticatedInstance;
        } else {
            securitySession = (SecuritySession) session.getAttribute("aksess.securitySession");
            if (securitySession == null){
                log.debug("Returning unauthenticatedInstance");
                securitySession = unauthenticatedInstance;
            } else {
                log.debug("Returning existing aksess.securitySession");
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
