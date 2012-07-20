package no.kantega.openaksess.search.solr.config;

import org.apache.commons.io.IOUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class SolrConfiguration {

    @Value("${appDir}/solr")
    private File solrHome;
    private File solrConfigFile;

    @Bean
    public SolrServer getSolrServer() throws IOException, SAXException, ParserConfigurationException {
        solrConfigFile = new File(solrHome, "solr.xml");
        initSolrConfigIfAbsent();
        CoreContainer container = new CoreContainer(solrHome.getAbsolutePath(), solrConfigFile);

        return new EmbeddedSolrServer(container, "oacore");
    }

    private void initSolrConfigIfAbsent() {
        if(!solrHome.exists()){
            boolean successfullMkdirs = solrHome.mkdirs();
            File confdir = new File(solrHome, "conf");
            File langdir = new File(confdir, "lang");
            successfullMkdirs &= confdir.mkdir();
            successfullMkdirs &= langdir.mkdir();

            if(!successfullMkdirs) {
                throw new IllegalStateException("Creation of solrhome unsuccessful");
            }

            try {
                copyAndCloseStreams(getClass().getResourceAsStream("/solrconfig/schema.xml"), new FileOutputStream(new File(confdir, "schema.xml")));
                copyAndCloseStreams(getClass().getResourceAsStream("/solrconfig/solrconfig.xml"), new FileOutputStream(new File(confdir, "solrconfig.xml")));
                copyAndCloseStreams(getClass().getResourceAsStream("/solrconfig/elevate.xml"), new FileOutputStream(new File(confdir, "elevate.xml")));
                copyAndCloseStreams(getClass().getResourceAsStream("/solrconfig/solr.xml"), new FileOutputStream(solrConfigFile));
                copyAndCloseStreams(getClass().getResourceAsStream("/solrconfig/lang/stopwords_en.txt"), new FileOutputStream(new File(langdir, "stopwords_en.txt")));
                copyAndCloseStreams(getClass().getResourceAsStream("/solrconfig/lang/stopwords_no.txt"), new FileOutputStream(new File(langdir, "stopwords_no.txt")));
            } catch (IOException e) {
                throw new IllegalStateException("Creation of solrhome unsuccessful", e);
            }

        }
    }

    private void copyAndCloseStreams(InputStream resourceAsStream, FileOutputStream output) throws IOException {
        IOUtils.copy(resourceAsStream, output);
        IOUtils.closeQuietly(resourceAsStream);
        IOUtils.closeQuietly(output);
    }
}
