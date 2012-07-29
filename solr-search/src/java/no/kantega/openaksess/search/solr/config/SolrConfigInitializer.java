package no.kantega.openaksess.search.solr.config;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SolrConfigInitializer {
    public static File initSolrConfigIfAbsent(File solrHome) {
        File solrConfigFile = new File(solrHome, "solr.xml");
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
                copyAndCloseStreams(SolrConfigInitializer.class.getResourceAsStream("/solrconfig/schema.xml"), new FileOutputStream(new File(confdir, "schema.xml")));
                copyAndCloseStreams(SolrConfigInitializer.class.getResourceAsStream("/solrconfig/solrconfig.xml"), new FileOutputStream(new File(confdir, "solrconfig.xml")));
                copyAndCloseStreams(SolrConfigInitializer.class.getResourceAsStream("/solrconfig/elevate.xml"), new FileOutputStream(new File(confdir, "elevate.xml")));
                copyAndCloseStreams(SolrConfigInitializer.class.getResourceAsStream("/solrconfig/solr.xml"), new FileOutputStream(solrConfigFile));
                copyAndCloseStreams(SolrConfigInitializer.class.getResourceAsStream("/solrconfig/lang/stopwords_en.txt"), new FileOutputStream(new File(langdir, "stopwords_en.txt")));
                copyAndCloseStreams(SolrConfigInitializer.class.getResourceAsStream("/solrconfig/lang/stopwords_no.txt"), new FileOutputStream(new File(langdir, "stopwords_no.txt")));
            } catch (IOException e) {
                throw new IllegalStateException("Creation of solrhome unsuccessful", e);
            }
        }
        return solrConfigFile;
    }

    private static void copyAndCloseStreams(InputStream resourceAsStream, FileOutputStream output) throws IOException {
        IOUtils.copy(resourceAsStream, output);
        IOUtils.closeQuietly(resourceAsStream);
        IOUtils.closeQuietly(output);
    }
}
