package no.kantega.openaksess.search.solr;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.IOException;

@Component
public class IndexDirDeleter {
    @PreDestroy
    public void deleteAppDir() throws IOException {
        FileUtils.deleteDirectory(AppDirPropertyPlaceHolder.appDir);
    }
}
