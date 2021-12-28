package no.kantega.openaksess.search.solr.config;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static java.util.Arrays.asList;

public class SolrConfigInitializer {
    private static final Logger log  = LoggerFactory.getLogger(SolrConfigInitializer.class);

    /**
     * Checks for the presense of solrHome. If it does not exist,
     * the neccesarry folders are created and the default configuration files
     * are copied into the appropriate directories.
     *
     * @param solrHome - The location of SolrHome. Typically ${appDir}/solr
     * @param disableUpdateSolrHome - if true the config files will not be updated if files in jar is
     *                              newer than the files in the config directory.
     * @return The file for solr.xml, solrHome/solr.xml
     */
    public static File initSolrConfigIfAbsent(File solrHome, boolean disableUpdateSolrHome) throws IOException {
        File solrConfigFile = new File(solrHome, "solr.xml");
        File coredir = new File(solrHome, "oacore");
        File confdir = new File(coredir, "conf");
        File langdir = new File(confdir, "lang");

        List<ConfigPair> configPairs = getConfigPairs(solrConfigFile, coredir,  confdir, langdir);
        if(!solrHome.exists()){
            createSolrHome(solrHome, confdir, langdir, configPairs);
        } else if(!disableUpdateSolrHome){
            for (ConfigPair configPair : configPairs) {
                copyToConfigDir(configPair);
            }
        }


        return solrConfigFile;
    }

    private static List<ConfigPair> getConfigPairs(File solrConfigFile, File coredir, File confdir, File langdir) {
        return asList(
                f("/solrconfig/solr.xml", solrConfigFile),
                f("/solrconfig/oacore/core.properties", new File(coredir, "core.properties")),
                f("/solrconfig/oacore/conf/schema.xml", new File(confdir, "schema.xml")),
                f("/solrconfig/oacore/conf/solrconfig.xml", new File(confdir, "solrconfig.xml")),
                f("/solrconfig/oacore/conf/elevate.xml", new File(confdir, "elevate.xml")),
                f("/solrconfig/oacore/conf/lang/stopwords_en.txt", new File(langdir, "stopwords_en.txt")),
                f("/solrconfig/oacore/conf/lang/stopwords_no.txt", new File(langdir, "stopwords_no.txt")));
    }

    private static ConfigPair f(String resourcePath, File targetFile) {
        return new ConfigPair(resourcePath, targetFile);
    }

    private static void createSolrHome(File solrHome, File confdir, File langdir, List<ConfigPair> configPairs) {
        log.info("Creating Solr home {}", solrHome.toString());
        boolean successfullMkdirs = solrHome.mkdirs();

        successfullMkdirs &= confdir.mkdirs();
        successfullMkdirs &= langdir.mkdirs();

        if(!successfullMkdirs) {
            throw new IllegalStateException("Creation of solrhome unsuccessful");
        }

        try {
            for (ConfigPair configPair : configPairs) {
                copyToConfigDir(configPair);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Creation of solrhome unsuccessful", e);
        }
    }

    private static void copyToConfigDir(ConfigPair configPair) throws IOException {
        if (configPair.targetFile.exists()) {
            log.info(configPair.targetFile.getAbsolutePath() + " exists, will not overwrite");
        } else {
            log.info("Copying {} to {}", configPair.resourcePath, configPair.targetFile);
            try(InputStream in = SolrConfigInitializer.class.getResourceAsStream(configPair.resourcePath);
                FileOutputStream out = new FileOutputStream(configPair.targetFile)){
                IOUtils.copy(in, out);
            }
        }
    }

    private static class ConfigPair {
        public final String resourcePath;
        public final File targetFile;

        public ConfigPair(String resourcePath, File targetFile) {
            this.resourcePath = resourcePath;
            this.targetFile = targetFile;
        }
    }
}
