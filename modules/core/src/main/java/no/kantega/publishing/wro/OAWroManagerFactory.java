package no.kantega.publishing.wro;

import no.kantega.publishing.spring.RootContext;
import no.kantega.publishing.wro.xmlmerge.XmlMerger;
import org.springframework.web.context.WebApplicationContext;
import ro.isdc.wro.manager.factory.ConfigurableWroManagerFactory;
import ro.isdc.wro.model.factory.WroModelFactory;
import ro.isdc.wro.model.factory.XmlModelFactory;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;

public class OAWroManagerFactory extends ConfigurableWroManagerFactory {

    private static final String OA_XML_CONFIG_FILE = "/WEB-INF/wro-oa.xml";
    private static final String PROJECT_XML_CONFIG_FILE = "/WEB-INF/wro-project.xml";

    @Override
    protected WroModelFactory newModelFactory() {

        return new XmlModelFactory(){
            @Override
            protected InputStream getModelResourceAsStream() throws IOException {

                WebApplicationContext wac = (WebApplicationContext) RootContext.getInstance();
                ServletContext servletContext = wac.getServletContext();

                return XmlMerger.merge(OA_XML_CONFIG_FILE, PROJECT_XML_CONFIG_FILE, servletContext);
            }
        };
    }

}
