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

package no.kantega.publishing.setup;

import org.apache.log4j.Logger;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;
import java.io.*;
import java.util.*;
import java.sql.Connection;
import java.sql.SQLException;

import no.kantega.publishing.spring.OpenAksessContextLoaderListener;
import no.kantega.publishing.spring.DataDirectoryContextListener;

/**
 */
public class SetupServlet extends HttpServlet {
    private Logger log = Logger.getLogger(getClass());

    private Map<String, String> driverClasses = new HashMap<String, String>();
    private ServletContext servletContext;
    private OpenAksessContextLoaderListener contextLoader;
    private File dataDirectory;
    private static final String CONFIG_FILE_ATTR = SetupServlet.class.getName() +".CONFIG_SOURCE";


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        driverClasses.put("mysql", "com.mysql.jdbc.Driver");
        driverClasses.put("mssql", "net.sourceforge.jtds.jdbc.Driver");
        servletContext = config.getServletContext();
        contextLoader = (OpenAksessContextLoaderListener) servletContext.getAttribute(OpenAksessContextLoaderListener.LISTENER_ATTR);
        dataDirectory  = (File)servletContext.getAttribute(DataDirectoryContextListener.DATA_DIRECTORY_ATTR);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        assertSetupNotOk();
        // Populate any existing database properties as default values in the form
        Properties props = contextLoader.getProperties();
        final String driver = props.getProperty("database.driver");
        if(!StringUtils.isEmpty(driver)) {
            if(driver.contains("mysql")) {
                req.setAttribute("driverName",  "mysql");
            } else if(driver.contains("jtds")) {
                req.setAttribute("driverName", "mssql");
            }
        }
        req.setAttribute("url", props.getProperty("database.url"));
        req.setAttribute("username", props.getProperty("database.username"));

        // Show the form
        req.getRequestDispatcher("/WEB-INF/setup/setup.jsp").forward(req, resp);
    }

    private void assertSetupNotOk() throws UnavailableException {
        if(! contextLoader.isSetupNeeded()) {
            throw new UnavailableException("Setup already done");
        }
    }

    @Override
    protected synchronized void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        assertSetupNotOk();
        
        // Verify that all fields are present
        String driverName = req.getParameter("driver");
        String url = req.getParameter("url");
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        List<String> errors = new ArrayList();

        if(StringUtils.isEmpty(driverName)) {
            errors.add("Please choose a database driver");
        } else if(!driverClasses.containsKey(driverName)) {
            errors.add("Unknown driver selected");
        }
        if(StringUtils.isEmpty(url)) {
            errors.add("Url is required");
        }
        if(StringUtils.isEmpty(username)) {
            errors.add("Username is required");
        }
        if(StringUtils.isEmpty(password)) {
            errors.add("Password is required");
        }

        // If all fields are present, we can create a test connection
        if(errors.size() == 0) {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName(driverClasses.get(driverName));
            dataSource.setUrl(url);
            dataSource.setUsername(username);
            dataSource.setPassword(password);

            final Connection connection;
            try {
                connection = dataSource.getConnection();
                connection.close();
            } catch (SQLException e) {
                log.error("Error connecting to database", e);
                errors.add("Could not connect to database: " + e.getMessage());
            }

        }

        // Redispatch to form if we have errors
        if(errors.size() > 0) {
            req.setAttribute("errors", errors);
            req.setAttribute("driverName", driverName);
            req.setAttribute("url", url);
            req.setAttribute("username", username);


            req.getRequestDispatcher("/WEB-INF/setup/setup.jsp").forward(req, resp);
        } else {
            // Otherwise, reconfigure aksess.conf, start application context and redirect to front page
            final String driver = driverClasses.get(driverName);
            reconfigure(driver, url, username, password, req.getContextPath());
            contextLoader.initContext();
            resp.sendRedirect(req.getContextPath());
        }


    }

    private void reconfigure(String driverClass, String url, String username, String password, String contextPath) {
        
        File aksessConf = new File(new File(dataDirectory, "conf"), "aksess.conf");



        try {
            List<String> lines = new ArrayList<String>();

            {
                // Read existing lines
                BufferedReader reader = null;

                try {
                    reader = new BufferedReader(aksessConf.exists() ? new InputStreamReader(new FileInputStream(aksessConf), "iso-8859-1") : new StringReader(""));

                    String line;
                    while((line = reader.readLine()) != null) {
                        lines.add(line);
                    }

                }finally {
                    if(reader != null) {
                        reader.close();
                    }
                }
            }



            // Remove database settings
            List<String> filteredLines = new ArrayList<String>();

            for(int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if(line.trim().startsWith("#")) {
                    filteredLines.add(line);
                } else if(!line.contains("=")) {
                    filteredLines.add(line);
                } else {
                    String[] nameValue = line.split("=");
                    if(nameValue.length >= 2) {
                        final String name = nameValue[0].trim();

                        if(!(name.equals("database.driver") ||
                                name.equals("database.url") ||
                                name.equals("database.username") ||
                                name.equals("database.password") ||
                                name.equals("location.contextpath"))) {
                            filteredLines.add(line);
                        }
                    } else {
                        filteredLines.add(line);
                    }
                }
            }

            // Add context path
            filteredLines.add(0, "location.contextpath=" + (contextPath.equals("") ? "/" : contextPath));
            // Add database setting
            filteredLines.add(0, "database.password=" + password);
            filteredLines.add(0, "database.username=" + username);
            filteredLines.add(0, "database.url=" + url);
            filteredLines.add(0, "database.driver=" + driverClass);


            // Write setting back out
            aksessConf.getParentFile().mkdirs();
            final StringWriter sw = new StringWriter();
            PrintWriter writer = new PrintWriter(sw);
            for(String line : filteredLines) {
                writer.println(line);
            }
            writer.close();


            final OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(aksessConf), "iso-8859-1");
            try {
                IOUtils.copy(new StringReader(sw.toString()), out);
            } finally {
                out.close();
            }

            // Are we told to copy updated settings back to a source file?
            String configSource = getServletContext().getInitParameter(CONFIG_FILE_ATTR);
            if(configSource != null) {
                File configSourceFile = new File(configSource);
                if(!configSourceFile.exists()) {
                    log.warn("Config source file does not exist, updated config not written back to source " + configSourceFile.getAbsolutePath());
                } else {

                    final OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(configSourceFile), "iso-8859-1");
                    try {
                        IOUtils.copy(new StringReader(sw.toString()), osw);
                    } finally {
                        osw.close();
                    }
                }
            }


        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
