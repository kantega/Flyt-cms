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

import no.kantega.publishing.common.data.Association;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.test.database.DerbyDatabaseCreator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import javax.sql.DataSource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class TrafficLoggerTest {

    private Collection<Pattern> searchEnginePatterns;
    private TrafficLoggerJdbcImpl trafficLoggerJdbc;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup(){
        trafficLoggerJdbc = new TrafficLoggerJdbcImpl();
        DataSource dataSource = new DerbyDatabaseCreator("aksess", getClass().getClassLoader().getResourceAsStream("dbschema/aksess-database-derby-test.sql")).createDatabase();

        jdbcTemplate = new JdbcTemplate(dataSource);
        trafficLoggerJdbc.setJdbcTemplate(jdbcTemplate);
        List<String> patternStrings = Arrays.asList(
        "http://.*google.*/search.*[\\?&]q=([^&$]*).*",
        "http://.*yahoo.*/search.*[\\?&]p=([^&$]*).*",
        "http://.*msn.*/results\\.aspx.*[\\?&]q=([^&$]*).*",
        "http://.*live.com.*/results\\.aspx.*[\\?&]q=([^&$]*).*",
        "http://.*kvasir.*/.*search.*[\\?&]searchExpr=([^&$]*).*",
        "http://.*ask.com/web.*[\\?&]q=([^&$]*).*");
        trafficLoggerJdbc.setSearchEnginePatterns(patternStrings);
        searchEnginePatterns = trafficLoggerJdbc.getSearchEnginePatterns();
    }

    @Test
    public void testLogContentAccess(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        String remoteAddr = "172.16.1.69";
        request.setRemoteAddr(remoteAddr);
        String refererHost = "search.yahoo.com";
        String refererQuery = "hey";
        String referer = "http://" + refererHost + "/search?p=" + refererQuery + "&fr=yfp-t-501&toggle=1&cop=mss&ei=UTF-8&vc=&fp_ip=NO";
        request.addHeader("Referer", referer);
        MockHttpSession session = new MockHttpSession();
        request.setSession(session);
        String useragent = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729)";
        request.addHeader("User-Agent", useragent);

        Content content = new Content();
        content.setId(1);
        content.setLanguage(666);
        Association association = new Association();
        association.setSiteId(1);
        content.setAssociations(Arrays.asList(association));
        trafficLoggerJdbc.log(content, request);

        List<Map<String,Object>> trafficlog = jdbcTemplate.query("select * from trafficlog", new ColumnMapRowMapper());
        assertEquals("Wrong number of entries returned", 1, trafficlog.size());
        Map<String, Object> trafficlogEntry = trafficlog.get(0);
        assertNotNull("Time was null", trafficlogEntry.get("Time"));
        assertEquals("ContentId was wrong", content.getId(), trafficlogEntry.get("ContentId"));
        assertEquals("Language was wrong", content.getLanguage(), trafficlogEntry.get("Language"));
        assertEquals("RemoteAddress was wrong", remoteAddr, trafficlogEntry.get("RemoteAddress"));
        assertEquals("SessionId was wrong", session.getId(), trafficlogEntry.get("SessionId"));
        assertEquals("SiteId was wrong", association.getSiteId(), trafficlogEntry.get("SiteId"));
        assertEquals("RefererHost was wrong", refererHost, trafficlogEntry.get("RefererHost"));
        assertEquals("RefererQuery was wrong", refererQuery, trafficlogEntry.get("RefererQuery"));
        assertEquals("IsSpider was wrong", 0, trafficlogEntry.get("IsSpider"));
        assertEquals("UserAgent was wrong", useragent, trafficlogEntry.get("UserAgent"));

    }

    @Test
    public void testGetRenfererInfoGoogle() throws UnsupportedEncodingException {
        String referer = "http://www.google.no/search?hl=en&client=firefox-a&rls=org.mozilla%3Anb-NO%3Aofficial&q=%22eirik+anders%22&btnG=Search";
        referer = URLDecoder.decode(referer, "utf-8");
        TrafficLoggerJdbcImpl.RefererInfo info = TrafficLoggerJdbcImpl.getRefererInfo(referer, searchEnginePatterns);
        assertEquals("www.google.no", info.getHost());
        assertEquals("\"eirik anders\"", info.getQuery());
        assertEquals(referer, info.getReferer());
    }

    @Test
    public void testGetRenfererInfoYahoo() {
        String referer = "http://search.yahoo.com/search?p=hey&fr=yfp-t-501&toggle=1&cop=mss&ei=UTF-8&vc=&fp_ip=NO";
        TrafficLoggerJdbcImpl.RefererInfo info = TrafficLoggerJdbcImpl.getRefererInfo(referer, searchEnginePatterns);
        assertEquals("search.yahoo.com", info.getHost());
        assertEquals("hey", info.getQuery());
        assertEquals(referer, info.getReferer());
    }

    @Test
    public void testGetRenfererInfoMSN() {
        String referer = "http://search.msn.com/results.aspx?q=tullball&FORM=MSNH";
        TrafficLoggerJdbcImpl.RefererInfo info = TrafficLoggerJdbcImpl.getRefererInfo(referer, searchEnginePatterns);
        assertEquals("search.msn.com", info.getHost());
        assertEquals("tullball", info.getQuery());
        assertEquals(referer, info.getReferer());
    }
}
