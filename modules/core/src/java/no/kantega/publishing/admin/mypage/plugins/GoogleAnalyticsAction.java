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

package no.kantega.publishing.admin.mypage.plugins;

import com.google.gdata.client.analytics.AnalyticsService;
import com.google.gdata.client.analytics.DataQuery;
import com.google.gdata.data.analytics.DataEntry;
import com.google.gdata.data.analytics.DataFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

import no.kantega.publishing.common.Aksess;
import no.kantega.commons.configuration.Configuration;

/**
 *
 */
public class GoogleAnalyticsAction implements Controller {

    private String resultsView;


    @Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();

        Configuration config = Aksess.getConfiguration();
        String username = config.getString("google.username");
        String password = config.getString("google.password");
        String tableId = config.getString("google.analytics.tableid");

        if ("".equals(username) || "".equals(password) || "".equals(tableId)) {
            model.put("errorMsg", "Insufficient account information."); // Utilstrekkelig kontoinformasjon
        } else {
            try {
                GAFacade facade = new GAFacade(username, password, tableId);
                model.put("pageviews", facade.getPageviews());
                model.put("usage", facade.getUsage());
            } catch (AuthenticationException e) {
                model.put("errorMsg", "Authentication failed : " + e.getMessage());
            } catch (MalformedURLException e) {
                model.put("errorMsg", "Malformed URL. Error message: " + e.getMessage());
            } catch (IOException e) {
                model.put("errorMsg", "Network error trying to retrieve feed: " + e.getMessage());
            } catch (ServiceException e) {
                model.put("errorMsg", "Analytics API responded with an error message: " + e.getMessage());
            }
        }
        return new ModelAndView(resultsView, model);
    }

    public void setResultsView(String resultsView) {
        this.resultsView = resultsView;
    }


    private class GAFacade {

        private String tableId;

        private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        private AnalyticsService analyticsService;
        private URL dataFeedUrl;


        private GAFacade(String username, String password, String tableId) throws AuthenticationException, MalformedURLException {
            this.tableId = tableId;
            dataFeedUrl = new URL("https://www.google.com/analytics/feeds/data");

            String companyId = "K_AS";
            String appName = "mp";
            String appVersion = "v1.0";
            String applicationName = companyId + "-" + appName + "-" + appVersion;

            // Service Object to work with the Google Analytics Data Export API.
            analyticsService = new AnalyticsService(applicationName);

            // Client Login Authorization.
            analyticsService.setUserCredentials(username, password);
        }

        private List<Map> getPageviews() throws IOException, ServiceException {
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

        private Map<String, Object> getUsage() throws IOException, ServiceException {
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
