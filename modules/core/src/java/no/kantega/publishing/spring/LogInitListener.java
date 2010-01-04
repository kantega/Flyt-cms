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
            writeDefaultConfigFile(configFile, logsDirectory);
        }
        Log.init(configFile);
    }

    private void writeDefaultConfigFile(File configFile, File logDirectory) {
        configFile.getParentFile().mkdirs();

        try {
            String content = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("no/kantega/publishing/log/default-log4j.xml"), "iso-8859-1");
            content = content.replaceAll("@logDirectory@", logDirectory.getAbsolutePath());
            final FileOutputStream out = new FileOutputStream(configFile);
            IOUtils.write(content, out, "iso-8859-1");
            out.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        LogManager.shutdown();
    }

}