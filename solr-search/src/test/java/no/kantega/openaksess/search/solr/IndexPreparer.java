package no.kantega.openaksess.search.solr;

import no.kantega.openaksess.search.solr.config.SolrConfiguration;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.tika.io.IOUtils;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Enumeration;
import java.util.Properties;
import java.util.zip.ZipEntry;

import static no.kantega.openaksess.search.solr.config.SolrConfigInitializer.initSolrConfigIfAbsent;

@Component
public class IndexPreparer extends PropertyPlaceholderConfigurer {

    private File tempDir = new File(System.getProperty("java.io.tmpdir"));
    private final File zipFile = new File(tempDir, "solr.zip");

    private SolrConfiguration solrConfiguration;

    public void unpackIndex() throws IOException {
        zipFile.deleteOnExit();
        InputStream zipFileStream = getClass().getResourceAsStream("/solr.zip");
        OutputStream out = new FileOutputStream(zipFile);
        copyAndClose(zipFileStream, out);

        ZipFile zip = new ZipFile(zipFile);
        Enumeration<ZipArchiveEntry> entries = zip.getEntries();
        while (entries.hasMoreElements()){
            ZipEntry entry = entries.nextElement();
            String currentEntry = entry.getName();
            File destFile = new File(tempDir, currentEntry);

            File destinationParent = destFile.getParentFile();

            destinationParent.mkdirs();

            if (!entry.isDirectory()){
                InputStream inputStream = zip.getInputStream((ZipArchiveEntry) entry);
                FileOutputStream fos = new FileOutputStream(destFile);
                copyAndClose(inputStream, fos);
            }
        }

    }

    private void copyAndClose(InputStream zipFileStream, OutputStream out) throws IOException {
        IOUtils.copy(zipFileStream, out);
        IOUtils.closeQuietly(out);
        IOUtils.closeQuietly(zipFileStream);
    }

    @Override
    protected Properties mergeProperties() throws IOException {
        File solrHome = new File(tempDir, "solr");
        solrHome.deleteOnExit();
        initSolrConfigIfAbsent(solrHome);
        unpackIndex();

        Properties properties = super.mergeProperties();
        properties.put("appDir", tempDir.getAbsolutePath());
        return properties;
    }
}
