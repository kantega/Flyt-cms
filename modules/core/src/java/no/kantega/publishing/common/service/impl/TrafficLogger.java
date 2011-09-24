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

package no.kantega.publishing.common.service.impl;

import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.exception.ConfigurationException;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.util.database.dbConnectionFactory;
import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.commons.util.HttpHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.URL;
import java.net.MalformedURLException;

public class TrafficLogger {
    private static final String SOURCE = "aksess.TrafficLogger";
    private static List searchEnginePatterns = new ArrayList();
    private static List botsAndSpiders = new ArrayList();
    private static String[] trafficLogIgnoreIPs;

    static {
        searchEnginePatterns.add(Pattern.compile("http://.*google.*/search.*[\\?&]q=([^&$]*).*"));
        searchEnginePatterns.add(Pattern.compile("http://.*yahoo.*/search.*[\\?&]p=([^&$]*).*"));
        searchEnginePatterns.add(Pattern.compile("http://.*msn.*/results\\.aspx.*[\\?&]q=([^&$]*).*"));
        searchEnginePatterns.add(Pattern.compile("http://.*live.com.*/results\\.aspx.*[\\?&]q=([^&$]*).*"));
        searchEnginePatterns.add(Pattern.compile("http://.*kvasir.*/.*search.*[\\?&]searchExpr=([^&$]*).*"));
        searchEnginePatterns.add(Pattern.compile("http://.*ask.com/web.*[\\?&]q=([^&$]*).*"));
    }

    static {
        botsAndSpiders.add("Gogglebot");
        botsAndSpiders.add("Yahoo! Slurp");
        botsAndSpiders.add("msnbot");
        botsAndSpiders.add("Ask Jeeves");
        botsAndSpiders.add("IPCheck Server Monitor");
        botsAndSpiders.add("Twiceler");
        botsAndSpiders.add("YodaoBot");
    }

    static {
        try {
            Configuration c = Aksess.getConfiguration();
            trafficLogIgnoreIPs = c.getStrings("trafficlog.ignoreips");
        } catch (SystemException e) {
            Log.error("", e, null, null);
        } catch (ConfigurationException e) {
            Log.error("", e, null, null);
        } catch (Exception e) {
            Log.error("", e, null, null);
        }
    }

    private static ExecutorService executor = Executors.newSingleThreadExecutor();


    public static void log(Content content, HttpServletRequest request) throws SystemException {
        HttpSession session = request.getSession(false);
        final long time = new Date().getTime();
        final int id = content.getId();
        final int language = content.getLanguage();
        final String remoteAddr = request.getRemoteAddr();
        final String fullReferer = request.getHeader("Referer");
        final String sessionId = session != null ? session.getId() : "";
        final int siteId = content.getAssociation().getSiteId();
        final String userAgent = request.getHeader("User-Agent");

        if (!HttpHelper.isAdminMode(request) && !ignoreIP(remoteAddr)) {

            // Run logging async on separate thread
            executor.execute( new Runnable() {
                public void run() {

                    Connection c = null;
                    try {

                        c = dbConnectionFactory.getConnection();
                        PreparedStatement st = c.prepareStatement("insert into trafficlog (Time, ContentId, Language, RemoteAddress, Referer, SessionId, SiteId, RefererHost, RefererQuery, IsSpider, UserAgent) values(?,?,?,?,?,?,?,?,?,?,?)");
                        st.setTimestamp(1, new java.sql.Timestamp(time));
                        st.setInt(2, id);
                        st.setInt(3, language);
                        st.setString(4, remoteAddr);

                        RefererInfo refInfo = getRefererInfo(fullReferer);
                        String referer = fullReferer;
                        if (referer != null && referer.length() > 255) {
                            referer = referer.substring(0, 254);
                        }

                        st.setString(5, referer);
                        st.setString(6, sessionId);
                        st.setInt(7, siteId);
                        st.setString(8, refInfo == null ? null  : refInfo.getHost());
                        st.setString(9, refInfo == null ? null : refInfo.getQuery());
                        st.setInt(10, isBotOrSpider(userAgent) ? 1:0);
                        st.setString(11, userAgent != null && userAgent.length() > 255 ? userAgent.substring(0,255) : userAgent);

                        st.execute();
                        st.close();
                    } catch (SQLException e) {
                        // Logger failed, not critical
                        Log.error(SOURCE, e, null, null);
                    } finally {
                        if(c != null) {
                            try {
                                c.close();
                            } catch (SQLException e) {
                                Log.error(SOURCE, e, null, null);
                            }
                        }
                    }

                }
            });
        }
    }

    private static boolean isBotOrSpider(String userAgent) {

        if (userAgent != null) {
            for (int i = 0; i < botsAndSpiders.size(); i++) {
                String bot = (String)botsAndSpiders.get(i);
                if (userAgent.indexOf(bot) != -1) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean ignoreIP(String remoteAddress) {
        if (remoteAddress != null && trafficLogIgnoreIPs != null) {
            for (int i = 0; i < trafficLogIgnoreIPs.length; i++) {
                if (remoteAddress.equals(trafficLogIgnoreIPs[i])) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void shutdownExecutor() {
        if(executor != null) {
            executor.shutdown();
        }
    }

    public static class RefererInfo {
        private String referer;
        private String host;
        private String query;

        public RefererInfo(String referer) {
            this.referer = referer;
        }

        public String getReferer() {
            return referer;
        }

        public void setReferer(String referer) {
            this.referer = referer;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }
    }

    public static RefererInfo getRefererInfo(String referer) {
        if(referer  == null) {
            return null;
        }

        URL url;
        try {
            url = new URL(referer);
        } catch (MalformedURLException e) {
            return null;
        }

        RefererInfo info = new RefererInfo(referer);
        info.setHost(url.getHost());

        for(Iterator i = searchEnginePatterns.iterator(); i.hasNext();) {
            Pattern p = (Pattern) i.next();
            Matcher matcher = p.matcher(referer);
            if(matcher.matches()) {
                String q = matcher.group(1);
                info.setQuery(q);
                break;
            }
        }
        return info;
    }
}

