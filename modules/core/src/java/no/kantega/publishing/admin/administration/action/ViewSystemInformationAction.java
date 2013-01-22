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
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.publishing.spring.RootContext;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Controller for viewing information about the application.
 */
public class ViewSystemInformationAction extends AbstractController {
    private String view;

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();

        if ("true".equals(request.getParameter("reload"))) {
            Configuration conf = (Configuration) RootContext.getInstance().getBean("aksessConfiguration");
            ConfigurationLoader loader = (ConfigurationLoader) RootContext.getInstance().getBean("aksessConfigurationLoader");
            conf.setProperties(loader.loadConfiguration());
        }

        try {
            Properties versionInfo = new Properties();
            versionInfo.load(getClass().getResourceAsStream("/aksess-version.properties"));
            model.put("aksessRevision", versionInfo.get("revision"));
            model.put("aksessTimestamp", parseDate((String) versionInfo.get("date")));
        } catch (IOException e) {
            Log.info(this.getClass().getName(), "aksess-version.properties not found", null, null);
        }
        try {
            Properties webappVersionInfo = new Properties();
            webappVersionInfo.load(getClass().getResourceAsStream("/aksess-webapp-version.properties"));
            model.put("webappRevision", webappVersionInfo.get("revision"));
            model.put("webappVersion", webappVersionInfo.get("version"));
            model.put("webappTimestamp", parseDate((String) webappVersionInfo.get("date")));
        } catch (IOException e) {
            Log.info(this.getClass().getName(), "aksess-webapp-version.properties not found", null, null);
        }

        model.put("aksessVersion", Aksess.getVersion());

        model.put("installDir", Configuration.getApplicationDirectory());

        model.put("databaseUrl", Aksess.getConfiguration().getString("database.url"));

        if (dbConnectionFactory.isPoolingEnabled()) {
            model.put("dbConnectionPoolEnabled", Boolean.TRUE);
        }

        DecimalFormat format = new DecimalFormat("#,###.##");
        long mb = 1024*1024;
        double free = Runtime.getRuntime().freeMemory()/(double)mb;
        double total = Runtime.getRuntime().totalMemory()/(double)mb;
        double max = Runtime.getRuntime().maxMemory()/(double)mb;

        model.put("freeMemory", format.format(free));
        model.put("totalMemory", format.format(total));
        model.put("maxMemory", format.format(max));

        ContentManagementService cms = new ContentManagementService(request);
        model.put("xmlCache", cms.getXMLCacheSummary());

        Configuration config = Aksess.getConfiguration();
        model.put("configProperties", config.getProperties());

        
        return new ModelAndView(view, model);
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
                Log.error("ViewSystemInformationAction", e1);
            }
        }

        return parsedDate;
    }

    public void setView(String view) {
        this.view = view;
    }
}
