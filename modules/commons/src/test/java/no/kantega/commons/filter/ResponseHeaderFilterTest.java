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

package no.kantega.commons.filter;

import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterChain;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class ResponseHeaderFilterTest {


    @Test
    public void shouldProcessGif() throws Exception {
        MockFilterConfig filterConfig = new MockFilterConfig();
        filterConfig.addInitParameter("extensions", "gif,css");
        filterConfig.addInitParameter("H_Pragma", "no-cache");
        filterConfig.addInitParameter("expiresInDays", "256");
        ResponseHeaderFilter filter = new ResponseHeaderFilter();
        filter.init(filterConfig);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("someuri.gif");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = new MockFilterChain();

        filter.doFilter(request, response, filterChain);

        assertEquals(2, response.getHeaderNames().size());
        assertEquals("no-cache", response.getHeader("Pragma"));
        DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date today = cal.getTime();
        Date expires = dateFormat.parse(response.getHeader("Expires").toString());
        cal.setTime(expires);
        cal.add(Calendar.DAY_OF_YEAR, -256);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        assertEquals(today, cal.getTime());

        filter.destroy();
    }

    @Test
    public void shouldProcessCss() throws Exception {
        MockFilterConfig filterConfig = new MockFilterConfig();
        filterConfig.addInitParameter("extensions", "gif,css");
        filterConfig.addInitParameter("H_Server", "CERN/3.0 libwww/2.17");
        filterConfig.addInitParameter("H_Expires", "Thu, 01 Dec 1994 16:00:00 GMT");
        ResponseHeaderFilter filter = new ResponseHeaderFilter();
        filter.init(filterConfig);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("this/is/a/stylesheet.css");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = new MockFilterChain();

        filter.doFilter(request, response, filterChain);

        assertEquals(2, response.getHeaderNames().size());
        assertEquals("CERN/3.0 libwww/2.17", response.getHeader("Server"));
        assertEquals("Thu, 01 Dec 1994 16:00:00 GMT", response.getHeader("Expires"));

        filter.destroy();
    }

    @Test
    public void shouldNotProcessPng() throws Exception {
        MockFilterConfig filterConfig = new MockFilterConfig();
        filterConfig.addInitParameter("extensions", "gif,css");
        filterConfig.addInitParameter("H_Pragma", "no-cache");
        filterConfig.addInitParameter("H_Expires", "Thu, 01 Dec 1994 16:00:00 GMT");
        filterConfig.addInitParameter("expiresInDays", "256");
        ResponseHeaderFilter filter = new ResponseHeaderFilter();
        filter.init(filterConfig);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("another/uri.png");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = new MockFilterChain();

        filter.doFilter(request, response, filterChain);

        assertEquals(0, response.getHeaderNames().size());

        filter.destroy();
    }

    @Test
    public void shouldProcessEverything() throws Exception {
        MockFilterConfig filterConfig = new MockFilterConfig();
        filterConfig.addInitParameter("H_Pragma", "no-cache");
        ResponseHeaderFilter filter = new ResponseHeaderFilter();
        filter.init(filterConfig);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("yet/another/uri");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = new MockFilterChain();

        filter.doFilter(request, response, filterChain);

        assertEquals(1, response.getHeaderNames().size());
        assertEquals("no-cache", response.getHeader("Pragma"));

        filter.destroy();
    }

}
