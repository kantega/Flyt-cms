package no.kantega.publishing.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

/**
 * De-register SQL drivers when context is destroyed.
 * <p/>
 * This prevents SEVERE warnings on shutdown in Tomcat 6/7.
 */
public class DatabaseDriversContextListener implements ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(DatabaseDriversContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Well-behaved JDBC drivers will register themselves with DriverManager.
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                log.info("Deregistering driver: {}", driver);
            } catch (SQLException e) {
                log.warn(String.format("Problem deregistering driver %s", driver), e);
            }
        }
    }

}
