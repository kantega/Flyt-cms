package no.kantega.openaksess.search.solr;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.tika.io.IOUtils;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.util.Properties;

@Component
public class IndexPreparer extends PropertyPlaceholderConfigurer {

    private File tempDir = new File(System.getProperty("java.io.tmpdir"));
    private File indexDir;

    @PostConstruct
    public void unpackIndex() throws IOException, ArchiveException {
        InputStream zipFileStream = getClass().getResourceAsStream("solr.zip");

        ArchiveInputStream in = new ArchiveStreamFactory().createArchiveInputStream("zip", zipFileStream);
        ZipArchiveEntry entry = (ZipArchiveEntry)in.getNextEntry();
        indexDir = new File(tempDir, entry.getName());
        OutputStream out = new FileOutputStream(indexDir);
        IOUtils.copy(in, out);
        IOUtils.closeQuietly(out);
        IOUtils.closeQuietly(in);
    }

    @PreDestroy
    public void cleanUp(){
        indexDir.delete();
    }

    @Override
    protected Properties mergeProperties() throws IOException {
        Properties properties = super.mergeProperties();
        properties.put("appDir", tempDir.getAbsolutePath());
        return properties;
    }
}
