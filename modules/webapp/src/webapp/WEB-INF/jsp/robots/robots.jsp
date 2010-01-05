<%@ page contentType="text/plain;charset=UTF-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.commons.util.URLHelper"%>
<%@ page import="java.io.BufferedReader"%>
<%@ page import="java.io.InputStream"%>
<%@ page import="java.io.InputStreamReader"%><%@ page import="java.io.IOException"%>
<%@ taglib uri="http://www.kantega.no/aksess/tags/aksess" prefix="aksess" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%--
  ~ Copyright 2009 Kantega AS
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~    http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<%
    Object crawlerSiteMapEnabledObject = request.getAttribute("crawlerSiteMapEnabled");
    Boolean crawlerSiteMapEnabled = crawlerSiteMapEnabledObject == null ? Boolean.valueOf(false) : (Boolean)crawlerSiteMapEnabledObject;

    // Se om det finnes en robots.txt fil på rota.
    // Hvis det gjør det, skriver vi ut innholdet av denne.
    try {
        InputStream is = config.getServletContext().getResourceAsStream("/robots.txt");
        if (is != null) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            while (rd.ready()) {
                out.println(rd.readLine());
            }
        }
    } catch (IOException e) {
        System.out.println("robots.jsp: Exception while reading robots.txt: " + e.getMessage());
    }

    // Hvis generering av sitemap.xml for crawlere er enabled,
    // legger vi til en linje som sier hvor denne filen ligger.
    if (crawlerSiteMapEnabled.booleanValue()) {
        out.println("Sitemap: " + URLHelper.getRootURL(request) + "sitemap.xml");
    }

    out.println("Disallow: /admin/");
    out.println("Disallow: /login/");
    out.println("Disallow: /Login.action");
%>
