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

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.util.StatusPrinter;
import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Ensure logging is configured and initiates logging.
 */
public class LogInitListener implements ServletContextListener {

    private LoggerContext loggerContext;

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        if (notAksessRun()) {
            final File dataDirectory  = (File) servletContextEvent.getServletContext().getAttribute(DataDirectoryContextListener.DATA_DIRECTORY_ATTR);

            if(dataDirectory == null) {
                throw new NullPointerException("dataDirectory attribute " + DataDirectoryContextListener.DATA_DIRECTORY_ATTR
                        +" was not set");
            }

            // Ensure log directory exists
            final File logsDirectory = new File(dataDirectory, "logs");
            logsDirectory.mkdirs();

            final File configFile = new File(new File(dataDirectory, "conf"),  "logback.groovy");

            if(!configFile.exists()) {
                writeDefaultConfigFile(configFile);
            }

            try {
                loggerContext.reset();
                loggerContext.putProperty("logdir", logsDirectory.getAbsolutePath());
                ContextInitializer contextInitializer = new ContextInitializer(loggerContext);
                contextInitializer.configureByResource(configFile.toURI().toURL());
                LoggerFactory.getLogger(getClass()).info("Configured logging using logdir {}", logsDirectory.getAbsolutePath());
            } catch (Exception je) {
                je.printStackTrace();
            }
            StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext);
        }
    }

    private boolean notAksessRun() {
        return System.getProperty("development", null) == null;
    }

    /**
     * Read the default log4.xml file from classpath and write it out to disk.
     * @param configFile where to write the default log4.xml
     */
    private void writeDefaultConfigFile(File configFile) {
        configFile.getParentFile().mkdirs();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("oa-logback.groovy");
             FileOutputStream out = new FileOutputStream(configFile)){

             IOUtils.copy(is, out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        if (loggerContext != null) {
            loggerContext.stop();
        }
    }

}
