package org.kantega.openaksess.plugins.metrics.dao;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class AppDirPropertyReplacer extends PropertyPlaceholderConfigurer {
    @Override
    protected Properties mergeProperties() throws IOException {
        Properties properties = super.mergeProperties();
        properties.setProperty("appDir", System.getProperty("java.io.tmpdir"));
        return properties;
    }

    public void cleanUp() {
        FileSystemUtils.deleteRecursively(new File(System.getProperty("java.io.tmpdir"), "metrics"));
    }
}
