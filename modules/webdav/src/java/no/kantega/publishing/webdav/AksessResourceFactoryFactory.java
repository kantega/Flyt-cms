package no.kantega.publishing.webdav;


import com.bradmcevoy.http.AuthenticationHandler;
import com.bradmcevoy.http.AuthenticationService;
import com.bradmcevoy.http.ResourceFactory;
import com.bradmcevoy.http.ResourceFactoryFactory;
import com.bradmcevoy.http.http11.auth.BasicAuthHandler;
import com.bradmcevoy.http.webdav.DefaultWebDavResponseHandler;
import com.bradmcevoy.http.webdav.WebDavResponseHandler;
import no.kantega.commons.log.Log;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class AksessResourceFactoryFactory implements ResourceFactoryFactory {
    private static AuthenticationService authenticationService;
    private static AksessResourceFactory resourceFactory;

    public ResourceFactory createResourceFactory() {
        return resourceFactory;
    }

    public WebDavResponseHandler createResponseHandler() {
        return new DefaultWebDavResponseHandler(authenticationService);
    }

    public void init() {
        Log.debug(this.getClass().getName(), "init");
        if( authenticationService == null ) {
            List<AuthenticationHandler> authenticationHandlers = new ArrayList<AuthenticationHandler>();
            authenticationHandlers.add(new BasicAuthHandler());
            authenticationService = new AuthenticationService(authenticationHandlers);
            resourceFactory = new AksessResourceFactory();
        }
    }

}
