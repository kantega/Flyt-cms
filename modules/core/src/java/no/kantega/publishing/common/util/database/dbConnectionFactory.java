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

package no.kantega.publishing.common.util.database;

import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.configuration.ConfigurationListener;
import no.kantega.commons.log.Log;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.exception.ConfigurationException;
import no.kantega.publishing.common.exception.DatabaseConnectionException;

import java.util.*;
import java.sql.SQLException;
import java.sql.Connection;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.io.IOUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 *
 */
public class dbConnectionFactory {
    private static final String SOURCE = "aksess.DataBaseConnectionFactory";

    private static String dbDriver = null;
    private static String dbUsername = null;
    private static String dbPassword = null;
    private static String dbUrl = null;
    private static DataSource ds = null;
    private static DataSource proxyDs = null;

    private static int dbMaxConnections = -1;
    private static int dbMaxWait = -1;

    private static boolean dbEnablePooling = false;
    private static boolean dbCheckConnections = true;

    public static int openedConnections = 0;
    public static int closedConnections = 0;
    public static Map connections  = Collections.synchronizedMap(new HashMap());

    private static boolean debugConnections = false;

    private static Configuration configuration;

    public static void loadConfiguration() {
        try {

            dbDriver = configuration.getString("database.driver", "com.mysql.jdbc.Driver");
            dbUrl = configuration.getString("database.url");
            dbUsername = configuration.getString("database.username", "root");
            dbPassword = configuration.getString("database.password", "");
            dbMaxConnections = configuration.getInt("database.maxconnections", 50);
            dbMaxWait = configuration.getInt("database.maxwait", 30);
            dbEnablePooling = configuration.getBoolean("database.enablepooling", true);
            dbCheckConnections = configuration.getBoolean("database.checkconnections", true);
            debugConnections = configuration.getBoolean("database.debugconnections", false);
            if (dbUrl == null || dbUsername == null || dbPassword == null) {

                String message = "Database configuration is not complete. The following settings are missing: ";

                List<String> props = new ArrayList<String>();
                if(dbUrl == null) {
                    props.add("database.url");
                }
                if(dbUsername == null) {
                    props.add("database.username");
                }
                if(dbPassword == null) {
                    props.add("database.password");
                }

                for(int i = 0; i < props.size(); i++) {
                    message +=props.get(i);
                    if(i != props.size()-1) {
                        message +=", ";
                    } else {
                        message +=".";
                    }
                }
                throw new ConfigurationException(message, SOURCE);
            }


            if (dbEnablePooling) {
                // Enable DBCP pooling
                BasicDataSource bds = new BasicDataSource();
                bds.setMaxActive(dbMaxConnections);
                bds.setMinIdle(dbMaxConnections);
                if (dbMaxWait != -1) {
                    bds.setMaxWait(1000*dbMaxWait);
                }

                bds.setDriverClassName(dbDriver);
                bds.setUsername(dbUsername);
                bds.setPassword(dbPassword);
                bds.setUrl(dbUrl);
                ds = bds;
            } else {
                DriverManagerDataSource dmds = new DriverManagerDataSource();
                dmds.setDriverClassName(dbDriver);
                dmds.setUrl(dbUrl);
                dmds.setUsername(dbUsername);
                dmds.setPassword(dbPassword);
                ds = dmds;
            }

            ensureDatabaseExists(ds);
            
            if(dbEnablePooling && dbCheckConnections) {
                BasicDataSource bds = (BasicDataSource) ds;
                // Gjør at connections frigjøres ved lukking fra database/brannmur
                bds.setValidationQuery("SELECT max(ContentId) from content");
                bds.setTimeBetweenEvictionRunsMillis(1000*60*2);
                bds.setMinEvictableIdleTimeMillis(1000*60*5);
            }

            if(debugConnections) {
                proxyDs = (DataSource) Proxy.newProxyInstance(DataSource.class.getClassLoader(), new Class[] {DataSource.class}, new DataSourceWrapper(ds));
            }

        } catch (Exception e) {
            Log.debug(SOURCE, "********* Klarte ikke å lese aksess.conf **********", null, null);
            Log.error(SOURCE, e, null, null);
            System.out.println("error:" + e);
        }
    }

    private static void ensureDatabaseExists(DataSource dataSource) {
        Connection c  = null;
        try {
            c = dataSource.getConnection();

             boolean hasTables = true;

             try {
                 c.createStatement().execute("SELECT max(ContentId) from content");
             } catch (SQLException e) {
                 hasTables = false;

             }

            if(!hasTables) {
                createTables(dataSource);
            }
        } catch (SQLException e) {
            throw new SystemException("Can't connect to database, please check configuration", SOURCE, e);
        } finally {
            if(c != null) {
                try {
                    c.close();
                } catch (SQLException e) {
                    // Ignore
                }
            }
        }
    }

    private static void createTables(DataSource dataSource) {
        Connection c = null;

        String productName = null;

        try {
            c = dataSource.getConnection();
            productName = c.getMetaData().getDatabaseProductName();

        } catch (SQLException e) {
            throw new SystemException("Error creating tables for Aksess", SOURCE, e);
        } finally {
            if(c != null) {
                try {
                    c.close();
                } catch (SQLException e) {
                    // Ignore
                }
            }
        }

        String dbType = null;
        if(productName.contains("Microsoft"))  {
            dbType = "mssql";
        } else if(productName.contains("Derby")) {
            dbType = "derby";
        } else if(productName.contains("MySQL")) {
            dbType = "mysql";
        } else if(productName.contains("Oracle")) {
            dbType = "oracle";
        } else if(productName.contains("PostgreSQL")) {
            dbType = "postgresql";
        } else {
            throw new RuntimeException("Unknow database product " + productName +", can't create database tables");
        }

        final URL resource = dbConnectionFactory.class.getClassLoader().getResource("/dbschema/aksess-database-" + dbType + ".sql");


        if(resource != null) {
            Log.info(SOURCE, "Creating tables from schema definition " + resource, null, null);
            final InputStream schema;
            try {
                schema = resource.openStream();
            } catch (IOException e) {
                throw new SystemException("Can't load schema resource " + resource, SOURCE, e);
            }
            try {

                final String[] statements = IOUtils.toString(schema).split(";");

                JdbcTemplate template = new JdbcTemplate(dataSource);
                for(String statement : statements) {
                    String[] lines = statement.split("\n");
                    String stripped = "";
                    for(String line : lines) {
                        if(line.trim().length()!=0 && !line.trim().startsWith("#") && !line.trim().startsWith("--")) {
                            stripped += line +"\n";
                        }
                    }
                    if(stripped.trim().length() > 0) {
                        template.execute(stripped);
                    }
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Connection getConnection() throws SystemException {
        try {
            Connection c;
            if(debugConnections) {
                c = proxyDs.getConnection();
            } else {
                c = ds.getConnection();
            }

            return c;
        } catch (SQLException se) {
            Log.info(SOURCE, "Klarte ikke å koble opp til databasen: url=" + dbUrl, null, null);
            Log.error(SOURCE, se, null, null);
            throw new DatabaseConnectionException(SOURCE, se);
        }
    }

    public static boolean isPoolingEnabled() {
        return dbEnablePooling;
    }

    public static boolean isDebugConnections() {
        return debugConnections;
    }

    public static int getMaxConnections() {
        return dbMaxConnections;
    }

    public static int getActiveConnections() {
        if (ds instanceof  BasicDataSource) {
            return ((BasicDataSource)ds).getNumActive();
        } else {
            return -1;
        }
    }

    public static int getIdleConnections() {
        if (ds instanceof BasicDataSource) {
            return ((BasicDataSource)ds).getNumIdle();
        } else {
            return -1;
        }
    }

    public static String getDriverName() {
        return dbDriver;
    }

    public static boolean isMySQL() {
        if (dbDriver.indexOf("mysql") != -1) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isOracle() {
        if (dbDriver.indexOf("oracle") != -1) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isPostgreSQL() {
        if (dbDriver.indexOf("postgresql") != -1) {
            return true;
        } else {
            return false;
        }
    }
    public static DataSource getDataSource() {
        if (debugConnections) {
            return proxyDs;
        } else {
            return ds;
        }
    }
    public static JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(ds);
    }

    public static void setConfiguration(Configuration configuration) {
        dbConnectionFactory.configuration = configuration;
        configuration.addConfigurationListener(new ConfigurationListener() {
            public void configurationRefreshed(Configuration configuration) {
                loadConfiguration();
            }
        });
    }
}

 class DataSourceWrapper implements InvocationHandler {
        DataSource dataSource;

        public DataSourceWrapper(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            if(method.getName().equalsIgnoreCase("getConnection")) {
                //System.out.println("ds: o/c: " +dbConnectionFactory.openedConnections +"/" + dbConnectionFactory.closedConnections +"(" +(dbConnectionFactory.openedConnections - dbConnectionFactory.closedConnections) +")");
                Connection c = (Connection)method.invoke(dataSource, objects);
                dbConnectionFactory.openedConnections++;
                StackTraceElement[] stacktrace = new Throwable().getStackTrace();
                dbConnectionFactory.connections.put(c, stacktrace);

                c = (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class[] {Connection.class}, new ConnectionWrapper(c));
                return c;
            } else {
                return method.invoke(dataSource, objects);
            }

        }
    }

class ConnectionWrapper implements InvocationHandler {
    Connection wrapped;
    ConnectionWrapper(Connection c) {
        wrapped = c;
    }
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            if(method.getName().equalsIgnoreCase("close")) {
                if(dbConnectionFactory.connections.get(wrapped) == null) {
                    StackTraceElement[] stackTraceElement = new Throwable().getStackTrace();
                    System.out.println("WOOOPS: Connection.close was already called!");
                    for (int i = 0; i < stackTraceElement.length && i < 3; i++) {
                        StackTraceElement e = stackTraceElement[i];
                        System.out.println(" - " +  e.getClassName() + "." + e.getMethodName() + " (" + e.getLineNumber() + ") <br>");
                    }
                } else {
                    dbConnectionFactory.closedConnections++;
                    dbConnectionFactory.connections.remove(wrapped);
                }
            }
            return method.invoke(wrapped, objects);
    }

}
