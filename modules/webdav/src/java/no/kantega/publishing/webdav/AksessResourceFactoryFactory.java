package no.kantega.publishing.webdav;


import com.bradmcevoy.http.AuthenticationHandler;
import com.bradmcevoy.http.AuthenticationService;
import com.bradmcevoy.http.ResourceFactory;
import com.bradmcevoy.http.ResourceFactoryFactory;
import com.bradmcevoy.http.http11.auth.BasicAuthHandler;
import com.bradmcevoy.http.webdav.DefaultWebDavResponseHandler;
import com.bradmcevoy.http.webdav.WebDavResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class AksessResourceFactoryFactory implements ResourceFactoryFactory {
    private static final Logger log = LoggerFactory.getLogger(AksessResourceFactoryFactory.class);
    private static AuthenticationService authenticationService;
    private static AksessResourceFactory resourceFactory;

    public ResourceFactory createResourceFactory() {
        return resourceFactory;
    }

    public WebDavResponseHandler createResponseHandler() {
        return new DefaultWebDavResponseHandler(authenticationService);
    }

    public void init() {
        log.debug( "init");
        if( authenticationService == null ) {
            List<AuthenticationHandler> authenticationHandlers = new ArrayList<AuthenticationHandler>();
            authenticationHandlers.add(new BasicAuthHandler());
            authenticationService = new AuthenticationService(authenticationHandlers);
            resourceFactory = new AksessResourceFactory();
        }
    }

}
