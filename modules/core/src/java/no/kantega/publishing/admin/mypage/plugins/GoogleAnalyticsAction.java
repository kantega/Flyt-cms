/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.admin.mypage.plugins;

import com.google.gdata.client.analytics.AnalyticsService;
import com.google.gdata.client.analytics.DataQuery;
import com.google.gdata.data.analytics.AccountEntry;
import com.google.gdata.data.analytics.AccountFeed;
import com.google.gdata.data.analytics.DataEntry;
import com.google.gdata.data.analytics.DataFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.util.LocaleLabels;
import no.kantega.publishing.common.Aksess;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class GoogleAnalyticsAction implements Controller {

    private String formView;
    private String resultsView;


    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        String view = formView;

        Configuration config = Aksess.getConfiguration();
        String username = config.getString("google.username");
        String password = config.getString("google.password");
        if ("".equals(username) || "".equals(password)) {
            model.put("errorMsg", LocaleLabels.getLabel("aksess.googleanalytics.error.noinfo", Aksess.getDefaultAdminLocale()));
        } else {
            try {
                GAFacade facade = new GAFacade(username, password);
                String tableId = request.getParameter("tableId");
                if (tableId == null || "".equals(tableId)) {
                    model.put("profiles", facade.getProfiles());
                } else {
                    view = resultsView;
                    model.put("pageviews", facade.getPageviews(tableId));
                    model.put("usage", facade.getUsage(tableId));
                }
            } catch (AuthenticationException e) {
                model.put("errorMsg", LocaleLabels.getLabel("aksess.googleanalytics.error.failed", Aksess.getDefaultAdminLocale()));
                Logger.getLogger(getClass()).error("Retrieving stats from Google Analytics failed: Authentication failed.", e);
            } catch (MalformedURLException e) {
                model.put("errorMsg", LocaleLabels.getLabel("aksess.googleanalytics.error.failed", Aksess.getDefaultAdminLocale()));
                Logger.getLogger(getClass()).error("Retrieving stats from Google Analytics failed: Malformed URL.", e);
            } catch (IOException e) {
                model.put("errorMsg", LocaleLabels.getLabel("aksess.googleanalytics.error.failed", Aksess.getDefaultAdminLocale()));
                Logger.getLogger(getClass()).error("Retrieving stats from Google Analytics failed: Network error trying to retrieve feed.", e);
            } catch (ServiceException e) {
                model.put("errorMsg", LocaleLabels.getLabel("aksess.googleanalytics.error.failed", Aksess.getDefaultAdminLocale()));
                Logger.getLogger(getClass()).error("Retrieving stats from Google Analytics failed: Analytics API responded with an error message.", e);
            }
        }
        return new ModelAndView(view, model);
    }

    public void setFormView(String formView) {
        this.formView = formView;
    }

    public void setResultsView(String resultsView) {
        this.resultsView = resultsView;
    }

    
    public class GAProfile {

        private String name;
        private String id;
        private String tableId;


        private GAProfile(String name, String id, String tableId) {
            this.name = name;
            this.id = id;
            this.tableId = tableId;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        public String getTableId() {
            return tableId;
        }

    }


    private class GAFacade {

        private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        private AnalyticsService analyticsService;
        private URL accountFeedUrl;
        private URL dataFeedUrl;


        private GAFacade(String username, String password) throws AuthenticationException, MalformedURLException {
            accountFeedUrl = new URL("https://www.google.com/analytics/feeds/accounts/default");
            dataFeedUrl = new URL("https://www.google.com/analytics/feeds/data");

            String companyId = "Kantega AS";
            String appName = "mypage";
            String appVersion = "v1.0";
            String applicationName = companyId + "-" + appName + "-" + appVersion;

            // Service Object to work with the Google Analytics Data Export API.
            analyticsService = new AnalyticsService(applicationName);

            // Client Login Authorization.
            analyticsService.setUserCredentials(username, password);
        }

        private List<GAProfile> getProfiles() throws IOException, ServiceException {
            List<GAProfile> profiles = new ArrayList<GAProfile>();
            AccountFeed accountFeed = analyticsService.getFeed(accountFeedUrl, AccountFeed.class);
            for (AccountEntry entry : accountFeed.getEntries()) {
                String name = entry.getTitle().getPlainText();
                String id = entry.getProperty("ga:profileId");
                String tableId = entry.getTableId().getValue();
                profiles.add(new GAProfile(name, id, tableId));
            }
            return profiles;
        }

        private List<Map> getPageviews(String tableId) throws IOException, ServiceException {
            List<Map> pageViews = new ArrayList<Map>();
            Calendar cal = Calendar.getInstance();
            Date now = cal.getTime();
            cal.add(Calendar.MONTH, -3);
            Date then = cal.getTime();
            
            // Request the top 10 page paths, page titles and pageviews,
            // in descending order by pageviews.
            DataQuery query = new DataQuery(dataFeedUrl);
            query.setStartDate(dateFormat.format(then));
            query.setEndDate(dateFormat.format(now));
            query.setDimensions("ga:pageTitle,ga:pagePath");
            query.setMetrics("ga:pageviews");
            query.setSort("-ga:pageviews");
            query.setMaxResults(10);
            query.setIds(tableId);

            // Make a request to the API.
            DataFeed dataFeed = analyticsService.getFeed(query.getUrl(), DataFeed.class);

            // Parse and return result
            for (DataEntry entry : dataFeed.getEntries()) {
                Map<String, String> result = new HashMap<String, String>();
                result.put("title", entry.stringValueOf("ga:pageTitle"));
                result.put("path", entry.stringValueOf("ga:pagePath"));
                result.put("views", entry.stringValueOf("ga:pageviews"));
                pageViews.add(result);
            }
            return pageViews;
        }

        private Map<String, Object> getUsage(String tableId) throws IOException, ServiceException {
            Map<String, Object> usage = new HashMap<String, Object>();
            Calendar cal = Calendar.getInstance();
            Date now = cal.getTime();
            cal.add(Calendar.MONTH, -1);
            Date then = cal.getTime();

            // Create query
            DataQuery query = new DataQuery(dataFeedUrl);
            query.setStartDate(dateFormat.format(then));
            query.setEndDate(dateFormat.format(now));
            query.setMetrics("ga:visits,ga:pageviews");
            query.setMaxResults(1);
            query.setIds(tableId);

            // Make a request to the API.
            DataFeed dataFeed = analyticsService.getFeed(query.getUrl(), DataFeed.class);

            // Parse result
            if (dataFeed.getEntries().size() > 0) {
                usage.put("visits", dataFeed.getEntries().get(0).stringValueOf("ga:visits"));
                usage.put("pageviews", dataFeed.getEntries().get(0).stringValueOf("ga:pageviews"));
            }


            // Create query
            query = new DataQuery(dataFeedUrl);
            Calendar c = Calendar.getInstance();
            Date b = c.getTime();
            c.add(Calendar.MONTH, -11);
            c.set(Calendar.DAY_OF_MONTH, 1);
            Date a = c.getTime();
            query.setStartDate(dateFormat.format(a));
            query.setEndDate(dateFormat.format(b));
            query.setMaxResults(12);
            query.setIds(tableId);
            query.setDimensions("ga:month,ga:year");
            query.setMetrics("ga:visits,ga:pageviews");
            query.setSort("ga:year,ga:month");

            // Make a request to the API.
            dataFeed = analyticsService.getFeed(query.getUrl(), DataFeed.class);

            // Parse result
            List<Map<String, String>> perMonthStats = new ArrayList<Map<String, String>>();
            for (DataEntry entry : dataFeed.getEntries()) {
                Map<String, String> e = new HashMap<String, String>();
                e.put("month", entry.stringValueOf("ga:month"));
                e.put("year", entry.stringValueOf("ga:year"));
                e.put("visits", entry.stringValueOf("ga:visits"));
                e.put("pageviews", entry.stringValueOf("ga:pageviews"));
                perMonthStats.add(e);
            }
            usage.put("perMonthStats", perMonthStats);


            // Create query
            query = new DataQuery(dataFeedUrl);
            query.setStartDate(dateFormat.format(then));
            query.setEndDate(dateFormat.format(now));
            query.setMaxResults(5);
            query.setIds(tableId);
            query.setDimensions("ga:browser");
            query.setMetrics("ga:visits");
            query.setSort("-ga:visits");

            // Make a request to the API.
            dataFeed = analyticsService.getFeed(query.getUrl(), DataFeed.class);

            // Parse result
            Map<String, String> browsers = new LinkedHashMap<String, String>();
            for (DataEntry entry : dataFeed.getEntries()) {
                browsers.put(entry.stringValueOf("ga:browser"), entry.stringValueOf("ga:visits"));
            }
            usage.put("browsers", browsers);

            return usage;
        }

    }


}
