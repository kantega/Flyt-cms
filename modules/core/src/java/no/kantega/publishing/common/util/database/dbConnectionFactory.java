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
import no.kantega.commons.exception.ConfigurationException;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.common.exception.DatabaseConnectionException;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.io.IOUtils;
import org.kantega.openaksess.dbmigrate.DbMigrate;
import org.kantega.openaksess.dbmigrate.ServletContextScriptSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class dbConnectionFactory {

    private static String dbDriver = null;
    private static String dbUsername = null;
    private static String dbPassword = null;
    private static String dbUrl = null;
    private static DataSource ds = null;
    private static DataSource proxyDs = null;

    private static int dbMaxConnections = -1;
    private static int dbMaxIdleConnections = -1;
    private static int dbMinIdleConnections = -1;
    private static int dbRemoveAbandonedTimeout = -1;
    private static int dbMaxWait = -1;
    private static int dbDefaultQueryTimeout;
    private static int dbTransactionIsolationLevel = Connection.TRANSACTION_NONE;

    private static boolean dbUseTransactions = false;

    private static boolean dbEnablePooling = false;
    private static boolean dbCheckConnections = true;

    private static boolean dbNTMLAuthentication = false;

    private static AtomicInteger openedConnections = new AtomicInteger();
    private static AtomicInteger closedConnections = new AtomicInteger();
    public static Map<Connection, StackTraceElement[]> connections  = new ConcurrentHashMap<>();

    private static boolean debugConnections = false;

    private static Configuration configuration;
    private static ServletContext servletContext;
    private static final Logger log = LoggerFactory.getLogger(dbConnectionFactory.class);
    private static boolean shouldMigrateDatabase;

    public static void loadConfiguration() {
        try {

            setConfiguration();

            verifyCompleteDatabaseConfiguration();

            DriverManagerDataSource rawDataSource = new DriverManagerDataSource();
            rawDataSource.setDriverClassName(dbDriver);
            rawDataSource.setUrl(dbUrl);

            if (!dbNTMLAuthentication) {
                rawDataSource.setUsername(dbUsername);
                rawDataSource.setPassword(dbPassword);
            }

            if (dbEnablePooling) {
                // Enable DBCP pooling
                BasicDataSource bds = new BasicDataSource();
                bds.setMaxTotal(dbMaxConnections);
                bds.setMaxIdle(dbMaxIdleConnections);
                bds.setMinIdle(dbMinIdleConnections);
                if (dbMaxWait != -1) {
                    bds.setMaxWaitMillis(1000 * dbMaxWait);
                }

                if(dbDefaultQueryTimeout != -1){
                    bds.setDefaultQueryTimeout(dbDefaultQueryTimeout);
                }

                bds.setDriverClassName(dbDriver);
                if (!dbNTMLAuthentication) {
                    bds.setUsername(dbUsername);
                    bds.setPassword(dbPassword);
                }
                bds.setUrl(dbUrl);

                if (dbUseTransactions) {
                    bds.setDefaultTransactionIsolation(dbTransactionIsolationLevel);
                }

                if(dbCheckConnections) {
                    // Gjør at connections frigjøres ved lukking fra database/brannmur
                    bds.setValidationQuery("SELECT max(ContentId) from content");
                    bds.setTimeBetweenEvictionRunsMillis(1000*60*2);
                    bds.setMinEvictableIdleTimeMillis(1000*60*5);
                    bds.setNumTestsPerEvictionRun(dbMaxConnections);
                    if (dbRemoveAbandonedTimeout > 0) {
                        bds.setRemoveAbandonedTimeout(dbRemoveAbandonedTimeout);
                        bds.setLogAbandoned(true);
                    }
                }

                ds = bds;
            } else {
                ds = rawDataSource;
            }

            // Use non-pooled datasource for table creation since validation query might fail
            ensureDatabaseExists(rawDataSource);
            if(shouldMigrateDatabase) {
                try {
                    migrateDatabase(servletContext, rawDataSource);
                } catch (Throwable cause) {
                    throw new DbMigrationException("Could not migrate datasource", cause);
                }
            }

            if (dbUseTransactions) {
                log.info( "Using transactions, database transaction isolation level set to " + dbTransactionIsolationLevel);
            } else {
                log.info( "Not using transactions");
            }

            if(debugConnections) {
                proxyDs = (DataSource) Proxy.newProxyInstance(DataSource.class.getClassLoader(), new Class[] {DataSource.class}, new DataSourceWrapper(ds));
            }

        } catch (DbMigrationException cause) {
            log.error(cause.getMessage(), cause);
            throw cause;
        } catch (Exception e) {
            log.error( "********* Klarte ikke å lese aksess.conf **********", e);
        }


    }

    private static void setConfiguration() throws ConfigurationException {
        dbDriver = configuration.getString("database.driver", "com.mysql.jdbc.Driver");
        dbUrl = configuration.getString("database.url");
        dbUsername = configuration.getString("database.username", "root");
        dbPassword = configuration.getString("database.password", "");
        dbMaxConnections = configuration.getInt("database.maxconnections", 50);
        dbMaxIdleConnections = configuration.getInt("database.maxidleconnections", 16);
        dbMinIdleConnections = configuration.getInt("database.minidleconnections", 8);
        dbMaxWait = configuration.getInt("database.maxwait", 30);
        dbDefaultQueryTimeout = configuration.getInt("database.defaultQueryTimeout", -1);
        dbRemoveAbandonedTimeout = configuration.getInt("database.removeabandonedtimeout", -1);
        dbEnablePooling = configuration.getBoolean("database.enablepooling", true);
        dbCheckConnections = configuration.getBoolean("database.checkconnections", true);
        debugConnections = configuration.getBoolean("database.debugconnections", false);
        shouldMigrateDatabase = configuration.getBoolean("database.migrate", true);
        dbNTMLAuthentication = configuration.getBoolean("database.useNTLMauthentication", false);
        dbUseTransactions = configuration.getBoolean("database.usetransactions", dbUseTransactions);
        dbTransactionIsolationLevel = configuration.getInt("database.transactionisolationlevel", Connection.TRANSACTION_READ_UNCOMMITTED);
    }

    private static void verifyCompleteDatabaseConfiguration() throws ConfigurationException {
        if (dbUrl == null || ((dbUsername == null || dbPassword == null) && !dbNTMLAuthentication)) {

            String message = "Database configuration is not complete. The following settings are missing: ";

            List<String> props = new ArrayList<>();
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
            throw new ConfigurationException(message);
        }
    }

    public static void migrateDatabase(ServletContext servletContext, DataSource dataSource) throws SQLException {
        DbMigrate migrate = new DbMigrate();

        try {
            new JdbcTemplate(dataSource).queryForObject("select count(version) from oa_db_migrations", Integer.class);
        } catch (DataAccessException e) {
            log.info("Automatic database migration cannot be enabled before the final manual upgrade is performed");
            return;
        }
        String root = "/WEB-INF/dbmigrate/";
        ServletContextScriptSource scriptSource = new ServletContextScriptSource(servletContext, root);

        Set<String> domainPaths = servletContext.getResourcePaths(root);
        List<String> domains = new ArrayList<>();
        // We want the oa domain first
        domains.add("oa");

        for (String domainPath : domainPaths) {
            if(domainPath.endsWith("/")) {
                // Remove last slash
                domainPath = domainPath.substring(0, domainPath.length()-1);
                String domain = domainPath.substring(domainPath.lastIndexOf('/')+1);
                if(!domain.startsWith(".") && !"oa".equals(domain)) {
                    domains.add(domain);
                }
            }
        }

        for (String domain : domains) {
            log.info("Migrating database domain '" + domain +"'");
            migrate.migrate(dataSource, domain, scriptSource);
        }

    }

    private static void ensureDatabaseExists(DataSource dataSource) {
        try (Connection c = dataSource.getConnection()){
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
            throw new SystemException("Can't connect to database, please check configuration", e);
        }
    }

    private static void createTables(DataSource dataSource) {
        String productName = null;

        try (Connection c = dataSource.getConnection()){
            productName = c.getMetaData().getDatabaseProductName();

        } catch (SQLException e) {
            throw new SystemException("Error creating tables for Aksess", e);
        }

        String dbType = getDBVendor(productName);

        final URL resource = dbConnectionFactory.class.getClassLoader().getResource("dbschema/aksess-database-" + dbType + ".sql");


        if(resource != null) {
            log.info( "Creating tables from schema definition " + resource);
            final InputStream schema;
            try {
                schema = resource.openStream();
            } catch (IOException e) {
                throw new SystemException("Can't load schema resource " + resource, e);
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

    private static String getDBVendor(String productName) {
        String dbType;
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
        return dbType;
    }

    public static Connection getConnection() throws SystemException {
        try {
            return debugConnections ? proxyDs.getConnection() : ds.getConnection();
        } catch (SQLException se) {
            log.error( "Unable to connect to database: url=" + dbUrl, se);
            throw new DatabaseConnectionException(se);
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
        return dbDriver.contains("mysql");

    }

    public static boolean isOracle() {
        return dbDriver.contains("oracle");
    }

    public static boolean useTransactions() {
        return dbUseTransactions;
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

    public static void setServletContext(ServletContext servletContext) {
        dbConnectionFactory.servletContext = servletContext;
    }

    public static void incrementOpenConnections() {
        openedConnections.incrementAndGet();
    }

    public static void incrementClosedConnections() {
        closedConnections.incrementAndGet();
    }

    public static int getOpenedConnections() {
        return openedConnections.get();
    }

    public static int getClosedConnections() {
        return closedConnections.get();
    }

    public static synchronized Map<Connection, StackTraceElement[]> getConnections() {
        return new HashMap<>(connections);
    }

    public static void closePool() {
        if(ds instanceof BasicDataSource) {
            try {
                ((BasicDataSource)ds).close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class DataSourceWrapper implements InvocationHandler {
        DataSource dataSource;

        public DataSourceWrapper(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            if(method.getName().equalsIgnoreCase("getConnection")) {
                Connection c = (Connection)method.invoke(dataSource, objects);
                StackTraceElement[] stacktrace = new Throwable().getStackTrace();
                dbConnectionFactory.connections.put(c, stacktrace);
                dbConnectionFactory.incrementOpenConnections();
                c = (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class[] {Connection.class}, new ConnectionWrapper(c));
                return c;
            } else {
                return method.invoke(dataSource, objects);
            }

        }

        private class ConnectionWrapper implements InvocationHandler {
            Connection wrapped;
            ConnectionWrapper(Connection c) {
                wrapped = c;
            }
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                if(method.getName().equalsIgnoreCase("close")) {
                    if(dbConnectionFactory.connections.get(wrapped) == null) {
                        StackTraceElement[] stackTraceElement = new Throwable().getStackTrace();
                        log.error( "WOOOPS: Connection.close was already called!");
                        for (int i = 0; i < stackTraceElement.length && i < 3; i++) {
                            StackTraceElement e = stackTraceElement[i];
                            log.error(" - " +  e.getClassName() + "." + e.getMethodName() + " (" + e.getLineNumber() + ") <br>");
                        }
                    } else {
                        dbConnectionFactory.incrementClosedConnections();
                        dbConnectionFactory.connections.remove(wrapped);
                    }
                }
                return method.invoke(wrapped, objects);
            }

        }
    }
    private static class DbMigrationException extends RuntimeException {

        public DbMigrationException() {}

        public DbMigrationException(String message) {
            super(message);
        }

        public DbMigrationException(String message, Throwable cause) {
            super(message, cause);
        }

        public DbMigrationException(Throwable cause) {
            super(cause);
        }

        public DbMigrationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
