package no.kantega.publishing.wro;

import ro.isdc.wro.config.Context;
import ro.isdc.wro.manager.factory.ServletContextAwareWroManagerFactory;
import ro.isdc.wro.model.factory.FallbackAwareXmlModelFactory;
import ro.isdc.wro.model.factory.WroModelFactory;

import javax.servlet.ServletContext;
import java.io.InputStream;

/**
 * Date: Aug 26, 2010
 * Time: 12:10:39 PM
 *
 * @author tarkil
 */
public class OAWroManagerFactory extends ServletContextAwareWroManagerFactory {

    public static final String OA_XML_CONFIG_FILE = "wro-oa.xml";


    @Override
    protected WroModelFactory newModelFactory(ServletContext servletContext) {
        return new FallbackAwareXmlModelFactory() {
            @Override
            protected InputStream getConfigResourceAsStream() {
                return Context.get().getServletContext().getResourceAsStream(
                        "/WEB-INF/" + OA_XML_CONFIG_FILE);
            }
        };
    }

}
