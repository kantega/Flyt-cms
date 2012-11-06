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

import junit.framework.TestCase;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


public class TrafficLoggerTest extends TestCase {
    public void testGetRenfererInfoGoogle() throws UnsupportedEncodingException {
        String referer = "http://www.google.no/search?hl=en&client=firefox-a&rls=org.mozilla%3Anb-NO%3Aofficial&q=%22eirik+anders%22&btnG=Search";
        referer = URLDecoder.decode(referer, "utf-8");
        TrafficLogger.RefererInfo info = TrafficLogger.getRefererInfo(referer);
        assertEquals("www.google.no", info.getHost());
        assertEquals("\"eirik anders\"", info.getQuery());
        assertEquals(referer, info.getReferer());
    }

    public void testGetRenfererInfoYahoo() {
        String referer = "http://search.yahoo.com/search?p=hey&fr=yfp-t-501&toggle=1&cop=mss&ei=UTF-8&vc=&fp_ip=NO";
        TrafficLogger.RefererInfo info = TrafficLogger.getRefererInfo(referer);
        assertEquals("search.yahoo.com", info.getHost());
        assertEquals("hey", info.getQuery());
        assertEquals(referer, info.getReferer());
    }


    public void testGetRenfererInfoMSN() {
        String referer = "http://search.msn.com/results.aspx?q=tullball&FORM=MSNH";
        TrafficLogger.RefererInfo info = TrafficLogger.getRefererInfo(referer);
        assertEquals("search.msn.com", info.getHost());
        assertEquals("tullball", info.getQuery());
        assertEquals(referer, info.getReferer());
    }
}
