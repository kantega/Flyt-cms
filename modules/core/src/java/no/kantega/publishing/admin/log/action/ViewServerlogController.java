package no.kantega.publishing.admin.log.action;

import com.google.common.base.Function;
import com.google.common.io.PatternFilenameFilter;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.transform;

@Controller
@RequestMapping("/admin/tools/logreader")
public class ViewServerlogController {
    private static final Logger log = LoggerFactory.getLogger(ViewServerlogController.class);

    @Value("${appDir}/logs")
    private File logFilesDir;

    @RequestMapping(value = "/logfiles.action", method = RequestMethod.GET)
    public @ResponseBody List<String> getLogFileNames(){
        return transform(Arrays.asList(logFilesDir.listFiles(new PatternFilenameFilter(".*\\.log"))), logFileTransformer);
    }

    @RequestMapping(value = "/logfiles/{logfile}.action", method = RequestMethod.GET)
    public @ResponseBody List<String> getLogLines(@PathVariable String logfile,
                                                  @RequestParam(required = false, defaultValue = "0") int startline,
                                                  @RequestParam(required = false, defaultValue = "50") int numberoflines){
        List<String> lines = new ArrayList<>(numberoflines);
        try(BufferedReader br = new BufferedReader(new FileReader(new File(logFilesDir, logfile)))){
            String line;
            int lineNumber = 0;
            int endline = startline + numberoflines;
            while ((line = br.readLine()) != null) {
                if(startline <= lineNumber){
                    lines.add("<div class=\"line\"><span class=\"linenumber\">" + lineNumber + "</span>" + line + "</div>");
                }
                lineNumber++;
                if(lineNumber > endline){
                    break;
                }
            }
        } catch (IOException e) {
            log.error("Error reading log file", e);
        }
        return lines;
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

    private final Function<File,String> logFileTransformer = new Function<File, String>() {
        @Override
        public String apply(File input) {
            return input.getName();
        }
    };
}
