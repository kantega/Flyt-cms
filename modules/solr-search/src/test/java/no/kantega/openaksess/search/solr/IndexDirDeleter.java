package no.kantega.openaksess.search.solr;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;

@Component
public class IndexDirDeleter {

    @Value("${appDir}")
    private File appDir;

    @PreDestroy
    public void deleteAppDir() throws IOException {
        FileUtils.deleteDirectory(appDir);
    }
}
