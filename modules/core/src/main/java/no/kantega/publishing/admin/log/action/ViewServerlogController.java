package no.kantega.publishing.admin.log.action;

import com.google.common.io.PatternFilenameFilter;
import no.kantega.publishing.common.Aksess;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/tools/logreader")
public class ViewServerlogController {
    private static final Logger log = LoggerFactory.getLogger(ViewServerlogController.class);

    private final File logFilesDir;

    /**
     * Create a new instance with a log dir set. Will try to read the "logDir" property from config, or fall back to
     * "appDir"/logs if not set.
     */
    public ViewServerlogController() {
        String logDir = Aksess.getConfiguration().getString("logDir");
        String defaultLogDir = Aksess.getConfiguration().getString("appDir") + "/logs";
        if (logDir != null) {
            File l = new File(logDir);
            if (!l.exists()) {
                log.info("Log dir {} does not exists, using default {}", defaultLogDir);
                logFilesDir = new File(defaultLogDir);
            } else {
                log.info("Using log dir {}", l);
                logFilesDir = l;
            }
        } else {
            log.info("Log dir not set, using default {}", defaultLogDir);
            logFilesDir = new File(defaultLogDir);
        }
    }

    @RequestMapping(value = "/logfiles.action", method = RequestMethod.GET)
    public @ResponseBody List<String> getLogFileNames(){
        return Arrays.asList(logFilesDir.listFiles(new PatternFilenameFilter(".*\\.log")))
                .stream()
                .map(File::getName)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/logfiles/{logfile}.action", method = RequestMethod.GET)
    public
    @ResponseBody
    ServerLogResponse getLogLines(@PathVariable String logfile,
                                  @RequestParam(required = false, defaultValue = "-1", value = "startline") int startlineParam,
                                  @RequestParam(required = false, defaultValue = "50") int numberoflines) {
        StringBuilder lines = new StringBuilder();
        File file = new File(logFilesDir, logfile);
        int numberOfLinesInFile = getNumberOfLines(file);
        ServerLogResponse response = new ServerLogResponse();
        response.setNumberOfLinesInFile(numberOfLinesInFile);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            int numberOfLinesReturned = 0;
            int startline = determineStartline(startlineParam, numberoflines, numberOfLinesInFile);
            int endline = startline + numberoflines;
            while ((line = br.readLine()) != null) {
                if (startline <= lineNumber) {
                    numberOfLinesReturned++;
                    lines.append("<div class=\"line\"><span class=\"linenumber\">").append(lineNumber)
                            .append("</span><span class=\"logline\">")
                            .append(HtmlUtils.htmlEscape(line)).append("</span></div>");
                }
                lineNumber++;
                if (lineNumber > endline) {
                    break;
                }
            }
            response.setLineNumber(lineNumber);
            response.setNumberOfLinesReturned(numberOfLinesReturned);
            response.setLines(lines.toString());
        } catch (IOException e) {
            log.error("Error reading log file", e);
        }
        return response;
    }

    @RequestMapping(value = "/logfiles/download/{logfile}.action", method = RequestMethod.GET)
    public void downloadLogFile(@PathVariable String logfile, HttpServletResponse response){
        File logOnDisk = new File(logFilesDir, logfile);
        response.setContentLength((int) logOnDisk.length());
        response.setContentType("text/plain");
        response.addHeader("Content-Disposition", "attachment; filename=\"" + logfile + "\"");
        try(InputStream is = new FileInputStream(logOnDisk); OutputStream os = response.getOutputStream()){
            IOUtils.copy(is, os);
        } catch (IOException e) {
            log.error("Error copying logfile", e);
        }
    }

    private int determineStartline(int startlineParam, int numberoflinesToGet, int numberOfLinesInFile) {
        return startlineParam >= 0 ? startlineParam : (numberOfLinesInFile - numberoflinesToGet);
    }

    private int getNumberOfLines(File file){
        try (LineNumberReader  lnr = new LineNumberReader(new FileReader(file))){
            while(lnr.skip(Long.MAX_VALUE) > 0) {}
            return lnr.getLineNumber();
        } catch (IOException e){
            log.error("Error counting lines", e);
            return 0;
        }
    }

    public class ServerLogResponse {

        private int numberOfLinesInFile;
        private int lineNumber;
        private String lines;
        private int numberOfLinesReturned;

        public void setNumberOfLinesInFile(int numberOfLinesInFile) {
            this.numberOfLinesInFile = numberOfLinesInFile;
        }

        public int getNumberOfLinesInFile() {
            return numberOfLinesInFile;
        }

        public void setLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public void setLines(String lines) {
            this.lines = lines;
        }

        public String getLines() {
            return lines;
        }

        public void setNumberOfLinesReturned(int numberOfLinesReturned) {
            this.numberOfLinesReturned = numberOfLinesReturned;
        }

        public int getNumberOfLinesReturned() {
            return numberOfLinesReturned;
        }
    }
}
