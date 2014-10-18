package no.kantega.publishing.web.servlet;


import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.spring.DataDirectoryContextListener;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StatusServlet extends HttpServlet {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private File file;
    private final String IS_AVAILABLE_TOKEN = "true";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        log.info("Initializing StatusServlet");
        setFileFromPropertOrDefault(config);
        try {
            FileUtils.write(file, IS_AVAILABLE_TOKEN);
        } catch (IOException e) {
            log.error("Error creating status file", e);
            throw new ServletException("Error creating status file", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean weAreAvailable = checkAvailabilityFile();
        boolean databaseIsUp = checkDatabase();

        if(weAreAvailable && databaseIsUp){
            respondOk(resp);
        } else {
            respondUnavailable(resp);
        }
    }

    private void setFileFromPropertOrDefault(ServletConfig config) {
        try {
            String fileFromProperty = System.getProperty("StatusServlet.availability.file");
            if (fileFromProperty == null) {
                File dataDir = (File) config.getServletContext().getAttribute(DataDirectoryContextListener.DATA_DIRECTORY_ATTR);
                file = new File(dataDir, "StatusServlet.isonline");
                log.info("Using statusfile {}", file.getAbsolutePath());
            } else {
                file = new File(fileFromProperty);
                log.info("Using statusfile from system property {}", fileFromProperty);
            }
        } catch (Exception e) {
            log.error("Error creating status file", e);
        }
    }

    private boolean checkDatabase() {
        try(Connection c = dbConnectionFactory.getConnection();
            PreparedStatement preparedStatement = c.prepareStatement("select 1")){
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean checkAvailabilityFile() {
        try {
            return IS_AVAILABLE_TOKEN.equals(FileUtils.readFileToString(file));
        } catch (IOException e) {
            log.warn("Error reading availability file");
            return false;
        }
    }

    private void respondUnavailable(HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        try(ServletOutputStream outputStream = resp.getOutputStream()){
            outputStream.print("SERVICE UNAVAILABLE");
        }
    }

    private void respondOk(HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        try(ServletOutputStream outputStream = resp.getOutputStream()){
            outputStream.print("OK");
        }
    }

    @Override
    public void destroy() {
        log.info("Deleted statusfile {}", file.delete());
        super.destroy();
    }
}

