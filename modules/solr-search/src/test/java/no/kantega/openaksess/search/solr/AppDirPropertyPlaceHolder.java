package no.kantega.openaksess.search.solr;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

@Component
public class AppDirPropertyPlaceHolder extends PropertyPlaceholderConfigurer {

    @Override
    protected Properties mergeProperties() throws IOException {
        Properties properties = super.mergeProperties();
        properties.put("appDir", Files.createTempDirectory("aksessSolrTest").toFile().getAbsolutePath());
        return properties;
    }
}
