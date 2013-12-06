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

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import no.kantega.commons.util.HttpHelper;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.traffic.TrafficLogger;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrafficLoggerJdbcImpl extends NamedParameterJdbcDaoSupport implements TrafficLogger {

    private Collection<Pattern> searchEnginePatterns = Collections.emptyList();
    private List<String> botsAndSpiders = Collections.emptyList();

    private List<String> trafficLogIgnoreIPs = Collections.emptyList();

    private boolean trafficlogEnabled = true;

    @Async
    public void log(@Nonnull Content content, @Nonnull HttpServletRequest request) {
        if (shouldLog(request)) {
            final String remoteAddr = request.getRemoteAddr();
            HttpSession session = request.getSession(false);
            Date time = new Date();
            int id = content.getId();
            int language = content.getLanguage();

            String fullReferer = request.getHeader("Referer");
            String sessionId = session != null ? session.getId() : "";
            int siteId = content.getAssociation().getSiteId();
            String userAgent = request.getHeader("User-Agent");

            RefererInfo refInfo = getRefererInfo(fullReferer);
            String referer = fullReferer;
            if (referer != null && referer.length() > 255) {
                referer = referer.substring(0, 254);
            }

            Map<String, Object> parameters = new HashMap<>();

            parameters.put("Time", time);
            parameters.put("ContentId", id);
            parameters.put("Language", language);
            parameters.put("RemoteAddress", remoteAddr);
            parameters.put("Referer", referer);
            parameters.put("SessionId", sessionId);
            parameters.put("SiteId", siteId);
            parameters.put("RefererHost", refInfo == null ? null : refInfo.getHost());
            parameters.put("RefererQuery", refInfo == null ? null : refInfo.getQuery());
            parameters.put("IsSpider", isBotOrSpider(userAgent));
            parameters.put("UserAgent", userAgent != null && userAgent.length() > 255 ? userAgent.substring(0, 255) : userAgent);
            getNamedParameterJdbcTemplate().update("insert into trafficlog (Time, ContentId, Language, RemoteAddress, Referer, SessionId, SiteId, RefererHost, RefererQuery, IsSpider, UserAgent) values(:Time, :ContentId, :Language, :RemoteAddress, :Referer, :SessionId, :SiteId, :RefererHost, :RefererQuery, :IsSpider, :UserAgent)",
                    parameters);

        }
    }

    private boolean shouldLog(HttpServletRequest request) {
        return trafficlogEnabled && !HttpHelper.isAdminMode(request) && !ignoreIP(request.getRemoteAddr());
    }

    private boolean isBotOrSpider(String userAgent) {

        if (userAgent != null) {
            for (String botsAndSpider : botsAndSpiders) {
                if (userAgent.contains(botsAndSpider)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean ignoreIP(String remoteAddress) {
        if (remoteAddress != null && trafficLogIgnoreIPs != null) {
            for (String trafficLogIgnoreIP : trafficLogIgnoreIPs) {
                if (remoteAddress.equals(trafficLogIgnoreIP)) {
                    return true;
                }
            }
        }

        return false;
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


    public RefererInfo getRefererInfo(String referer) {
        return getRefererInfo(referer, searchEnginePatterns);
    }

    public static RefererInfo getRefererInfo(String referer, Collection<Pattern> searchEnginePatterns) {
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

        for (Pattern searchEnginePattern : searchEnginePatterns) {
            Matcher matcher = searchEnginePattern.matcher(referer);
            if (matcher.matches()) {
                String q = matcher.group(1);
                info.setQuery(q);
                break;
            }
        }
        return info;
    }

    public void setSearchEngineStringPatterns(List<String> searchEnginePatterns){
        Function<String, Pattern> stringToPatternTransform = new Function<String, Pattern>() {
            @Override
            public Pattern apply(@Nullable String pattern) {
                return Pattern.compile(pattern);
            }
        };
        this.searchEnginePatterns = Collections2.transform(searchEnginePatterns, stringToPatternTransform);
    }

    public void setBotsAndSpiders(List<String> botsAndSpiders){
        this.botsAndSpiders = botsAndSpiders;
    }

    public void setTrafficlogEnabled(boolean trafficlogEnabled) {
        this.trafficlogEnabled = trafficlogEnabled;
    }

    public void setTrafficLogIgnoreIPs(List<String> trafficLogIgnoreIPs) {
        this.trafficLogIgnoreIPs = trafficLogIgnoreIPs;
    }

    public Collection<Pattern> getSearchEnginePatterns() {
        return searchEnginePatterns;
    }
}

