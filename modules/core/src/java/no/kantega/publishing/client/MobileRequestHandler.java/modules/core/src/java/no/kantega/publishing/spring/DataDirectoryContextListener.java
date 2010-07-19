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

import org.simplericity.datadirlocator.DefaultDataDirectoryLocator;
import org.simplericity.datadirlocator.DataDirectoryLocatorStrategy;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContext;
import java.io.File;
import java.util.Properties;

/**
 */
public class DataDirectoryContextListener implements ServletContextListener {

    public static final String DATA_DIRECTORY_ATTR = DataDirectoryContextListener.class.getName() +"_DATA_DIRECTORY";

    public void contextInitialized(ServletContextEvent sce) {
        final ServletContext servletContext = sce.getServletContext();
        File dataDirectory = getDataDirectory(servletContext);
        makeFolders(dataDirectory);
        servletContext.setAttribute(DATA_DIRECTORY_ATTR, dataDirectory);
    }

    private void makeFolders(File dataDirectory) {
        dataDirectory.mkdirs();
        new File(dataDirectory, "uploads").mkdirs();
        new File(dataDirectory, "logs").mkdirs();
        new File(dataDirectory, "index").mkdirs();
    }

    public void contextDestroyed(ServletContextEvent sce) {
        sce.getServletContext().removeAttribute(DATA_DIRECTORY_ATTR);
    }

    private File getDataDirectory(ServletContext servletContext) {
        DefaultDataDirectoryLocator locator = new DefaultDataDirectoryLocator();

        locator.setContextParamName("kantega.appDir");
        locator.setSystemProperty("kantega.appDir");

        locator.addStrategy(new LegacyKantegaDirLocatorStrategy());
        locator.setServletContext(servletContext);
        File dataDir = locator.locateDataDirectory();
        return dataDir;
    }

    private class LegacyKantegaDirLocatorStrategy implements DataDirectoryLocatorStrategy {

        private static final String CONFIGFILE = "kantega.properties";
        private static final String DEFAULT_WIN_DIR = "C:\\Kantega";
        private static final String DEFAULT_UNIX_DIR = "/usr/local/kantega";


        public File locateDataDirectory() {

            File kantegaDir = new File(getKantegaDir());

            if(!kantegaDir.exists()) {
                throw new RuntimeException("Could not find kantega.dir " + kantegaDir);
            }

            String app = getApplication();


            if(app == null || app.length() == 0) {
                return kantegaDir;
            } else {

                File appDir = new File(kantegaDir, app);

                if(!appDir.exists()) {
                    throw new RuntimeException("Could not find Aksess application directory " + appDir);
                }

                return appDir;
            }



        }

        private String getKantegaDir() {
            String ps = File.separator;

            String kantegaDir = null;
            try {
                Properties p = new Properties();
                p.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIGFILE));
                kantegaDir = p.getProperty("kantega.dir");
            } catch (Exception e) {
                // Gjør ingenting, bruker default
            }

            if (kantegaDir == null || kantegaDir.length() == 0) {
                kantegaDir = System.getProperty("kantega.dir");
                if(kantegaDir == null) {
                    if (ps.equals("/")) {
                        kantegaDir = DEFAULT_UNIX_DIR;
                    } else {
                        kantegaDir = DEFAULT_WIN_DIR;
                    }
                }

            }
            if(!kantegaDir.endsWith(ps)) {
                kantegaDir += ps;
            }

            return kantegaDir;
        }


        public String getApplication() {
            String application = null;
            try {
                Properties p = new Properties();
                p.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIGFILE));
                application = p.getProperty("application.name");
                if (application == null) {
                    application = p.getProperty("application");
                }
            } catch (Exception e) {
                // Do nothing, file could not be found or read
            }

            return application;
        }
    }
}
