/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.spring;

import org.apache.log4j.LogManager;
import org.apache.commons.io.IOUtils;
import no.kantega.commons.log.Log;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Ensure logging is configured and initiates logging.
 */
public class LogInitListener implements ServletContextListener {


    public void contextInitialized(ServletContextEvent servletContextEvent) {
        final File dataDirectory  = (File) servletContextEvent.getServletContext().getAttribute(DataDirectoryContextListener.DATA_DIRECTORY_ATTR);

        if(dataDirectory == null) {
            throw new NullPointerException("dataDirectory attribute " + DataDirectoryContextListener.DATA_DIRECTORY_ATTR
                    +" was not set");
        }

        // Ensure log directory exists
        final File logsDirectory = new File(dataDirectory, "logs");
        logsDirectory.mkdirs();

        final File configFile = new File(new File(dataDirectory, "conf"),  "log4j.xml");

        if(!configFile.exists()) {
            writeDefaultConfigFile(configFile);
        }
        Log.init(configFile, logsDirectory);
    }

    /**
     * Read the default log4.xml file from classpath and write it out to disk.
     * @param configFile where to write the default log4.xml
     */
    private void writeDefaultConfigFile(File configFile) {
        configFile.getParentFile().mkdirs();

        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("no/kantega/publishing/log/default-log4j.xml");

            final FileOutputStream out = new FileOutputStream(configFile);
            try {
                IOUtils.copy(is, out);
            } finally {
                IOUtils.closeQuietly(out);
                IOUtils.closeQuietly(is);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        LogManager.shutdown();
    }

}