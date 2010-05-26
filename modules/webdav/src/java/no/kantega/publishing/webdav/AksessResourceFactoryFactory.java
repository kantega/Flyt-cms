package no.kantega.publishing.webdav;


import com.bradmcevoy.http.ResourceFactoryFactory;
import com.bradmcevoy.http.ResourceFactory;
import com.bradmcevoy.http.AuthenticationService;
import com.bradmcevoy.http.webdav.WebDavResponseHandler;
import com.bradmcevoy.http.webdav.DefaultWebDavResponseHandler;
import no.kantega.commons.log.Log;

/**
 *
 */
public class AksessResourceFactoryFactory implements ResourceFactoryFactory {
    private static AuthenticationService authenticationService;
    private static AksessResourceFactory resourceFactory;

    @Override
    public ResourceFactory createResourceFactory() {
        return resourceFactory;
    }

    @Override
    public WebDavResponseHandler createResponseHandler() {
        return new DefaultWebDavResponseHandler(authenticationService);
    }

    @Override
    public void init() {
        Log.debug(this.getClass().getName(), "init");
        if( authenticationService == null ) {
            authenticationService = new AuthenticationService();
            resourceFactory = new AksessResourceFactory();
        }
    }

}
