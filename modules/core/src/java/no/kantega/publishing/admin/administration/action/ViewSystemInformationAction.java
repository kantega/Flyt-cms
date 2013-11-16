/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.admin.administration.action;

import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.configuration.ConfigurationLoader;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.spring.RootContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Controller for viewing information about the application.
 */
public class ViewSystemInformationAction extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(ViewSystemInformationAction.class);
    private String view;

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<>();

        if ("true".equals(request.getParameter("reload"))) {
            Configuration conf = (Configuration) RootContext.getInstance().getBean("aksessConfiguration");
            ConfigurationLoader loader = (ConfigurationLoader) RootContext.getInstance().getBean("aksessConfigurationLoader");
            conf.setProperties(loader.loadConfiguration());
        }

        addOAAndWebappVersionInformation(model);

        model.put("aksessVersion", Aksess.getVersion());

        model.put("installDir", Configuration.getApplicationDirectory());

        model.put("databaseUrl", Aksess.getConfiguration().getString("database.url"));

        if (dbConnectionFactory.isPoolingEnabled()) {
            model.put("dbConnectionPoolEnabled", Boolean.TRUE);
        }

        addMemoryInformation(model);

        ContentManagementService cms = new ContentManagementService(request);
        model.put("xmlCache", cms.getXMLCacheSummary());

        Configuration config = Aksess.getConfiguration();
        model.put("configProperties", config.getProperties());

        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        addVMStartDate(model, runtimeMXBean);

        addJVMInformaion(model, runtimeMXBean);

        return new ModelAndView(view, model);
    }

    private void addOAAndWebappVersionInformation(Map<String, Object> model) {
        try {
            Properties versionInfo = new Properties();
            versionInfo.load(getClass().getResourceAsStream("/aksess-version.properties"));
            model.put("aksessRevision", versionInfo.get("revision"));
            model.put("aksessTimestamp", parseDate((String) versionInfo.get("date")));
        } catch (IOException e) {
            log.info( "aksess-version.properties not found");
        }
        try {
            Properties webappVersionInfo = new Properties();
            webappVersionInfo.load(getClass().getResourceAsStream("/aksess-webapp-version.properties"));
            model.put("webappRevision", webappVersionInfo.get("revision"));
            model.put("webappVersion", webappVersionInfo.get("version"));
            model.put("webappTimestamp", parseDate((String) webappVersionInfo.get("date")));
        } catch (IOException e) {
            log.info( "aksess-webapp-version.properties not found");
        }
    }

    private void addVMStartDate(Map<String, Object> model, RuntimeMXBean runtimeMXBean) {
        long jvmStartTime = runtimeMXBean.getStartTime();
        model.put("jvmStartDate", new Date(jvmStartTime));
    }

    private void addJVMInformaion(Map<String, Object> model, RuntimeMXBean runtimeMXBean) {
        model.put("vmName", runtimeMXBean.getVmName());
        model.put("vmVendor", runtimeMXBean.getVmVendor());
        model.put("vmVersion", runtimeMXBean.getVmVersion());
        model.put("javaVersion", System.getProperty("java.version"));
        model.put("tmpDir", System.getProperty("java.io.tmpdir"));
    }

    private void addMemoryInformation(Map<String, Object> model) {
        DecimalFormat format = new DecimalFormat("#,###.##");
        long mb = 1024*1024;
        Runtime runtime = Runtime.getRuntime();
        double free = runtime.freeMemory()/(double)mb;
        double total = runtime.totalMemory()/(double)mb;
        double max = runtime.maxMemory()/(double)mb;

        model.put("freeMemory", format.format(free));
        model.put("totalMemory", format.format(total));
        model.put("maxMemory", format.format(max));
    }

    private Date parseDate(String date) {
        DateFormat svnTimestampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
        Date parsedDate = null;
        try {
            parsedDate = svnTimestampFormat.parse(date);
        } catch (ParseException e) {
            DateFormat timestampFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            try {
                parsedDate = timestampFormat.parse(date);
            } catch (ParseException e1) {
                timestampFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
                try {
                    parsedDate = timestampFormat.parse(date);
                } catch (ParseException e2) {
                    log.error("Could not parse " + date, e2);
                }
            }
        }

        return parsedDate;
    }

    public void setView(String view) {
        this.view = view;
    }
}
