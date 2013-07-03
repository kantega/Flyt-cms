package org.kantega.openaksess.plugins.groovyconsole;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;

/**
 *
 */
@Controller
@RequestMapping("/admin/groovyauth.action")
public class GroovyAuthorizationController {


    private String authorizationView = "org/kantega/openaksess/plugins/groovyconsole/views/authorization.vm";

    private File authorizationTokenFile;

    private SecureRandom random = new SecureRandom();
    private static final long ONE_HOUR = 1000*60*60;


    @RequestMapping(method = RequestMethod.GET)
    public String show() throws IOException {
        ensureAuthorizationTokenExists();
        return authorizationView;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handle(@RequestParam String token, HttpSession session) throws IOException {
        if(!token.equals(FileUtils.readFileToString(authorizationTokenFile, "utf-8"))) {
            return authorizationView;
        } else {
            session.setAttribute(GroovyAuthorizationInterceptor.AUTHORIZED_KEY, Boolean.TRUE);
            return "redirect:groovy.action";
        }
    }

    private void ensureAuthorizationTokenExists() throws IOException {
        if(!authorizationTokenFile.exists() || isTooOld(authorizationTokenFile)) {
            long token = Math.abs(random.nextLong());
            FileUtils.writeStringToFile(authorizationTokenFile, Long.toString(token), "utf-8");
        }
    }

    private boolean isTooOld(File authorizationTokenFile) {
        return System.currentTimeMillis() - authorizationTokenFile.lastModified() > ONE_HOUR;
    }

    public void setAuthorizationTokenFile(File authorizationTokenFile) {
        this.authorizationTokenFile = authorizationTokenFile;
    }
}
