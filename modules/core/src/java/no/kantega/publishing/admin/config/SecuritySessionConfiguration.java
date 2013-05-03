package no.kantega.publishing.admin.config;

import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.security.SecuritySession;
import no.kantega.publishing.security.data.Role;
import no.kantega.publishing.security.data.User;
import no.kantega.publishing.security.realm.SecurityRealm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.servlet.http.HttpSession;

@Configuration
public class SecuritySessionConfiguration {
    @Autowired
    private SecurityRealm securityRealm;

    @Bean
    @Scope("request")
    public SecuritySession getSecuritySession(HttpSession session){
        SecuritySession securitySession;
        if(session == null){
            securitySession = createNewUnauthenticatedInstance();
        }else {
            securitySession = (SecuritySession) session.getAttribute("aksess.securitySession");
            if(securitySession == null){
                securitySession = createNewUnauthenticatedInstance();
            }
        }
        return securitySession;
    }

    private SecuritySession createNewUnauthenticatedInstance() {
        User unauthenticatedUser = new User();
        unauthenticatedUser.setGivenName("Aksess - Unauthenticated");
        unauthenticatedUser.setSurname("CMS");
        unauthenticatedUser.setId("adminUnauthenticated");

        Role role = new Role();
        role.setId(Aksess.getEveryoneRole());
        role.setName(Aksess.getEveryoneRole());
        unauthenticatedUser.addRole(role);

        return new SecuritySession(securityRealm, unauthenticatedUser);
    }
}
