package no.kantega.openaksess.search.solr;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

@Component
public class AppDirPropertyPlaceHolder extends PropertyPlaceholderConfigurer {
    public static File appDir = new File(System.getProperty("java.io.tmpdir"), "aksesstest");

    @Override
    protected Properties mergeProperties() throws IOException {
        Properties properties = super.mergeProperties();
        properties.put("appDir", appDir.getAbsolutePath());
        return properties;
    }
}
