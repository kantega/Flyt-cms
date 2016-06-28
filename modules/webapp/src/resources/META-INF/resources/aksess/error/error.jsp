<%@ page contentType="text/html;charset=utf-8" language="java" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ page import="no.kantega.commons.util.URLHelper,
                 no.kantega.publishing.common.exception.ExceptionHandler"%>
<%@ page import="no.kantega.publishing.security.SecuritySession"%>
<%@ page import="org.slf4j.Logger"%>
<%@ page import="org.slf4j.LoggerFactory"%>
<%@ page import="java.text.DateFormat"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Date" %>
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
    Logger log = LoggerFactory.getLogger("no.kantega.openaksess.jsp.error");
    request.setAttribute("aksess_locale", Aksess.getDefaultAdminLocale());
    response.setDateHeader("Expires", 0);
    ExceptionHandler handler = (ExceptionHandler)request.getSession(true).getAttribute("handler");

    String error = handler.getMessage();

    String details = handler.getDetails();

    DateFormat df = new SimpleDateFormat("yyyy.MM.dd.MM - HH:mm");
    String timestamp = df.format(new Date());

    String ident = "";    String email = "";
    SecuritySession secSession = SecuritySession.getInstance(request);
    if (secSession != null && secSession.getUser() != null) {
        ident = secSession.getUser().getId();
        email = secSession.getUser().getEmail();
    }
    String url = URLHelper.getRootURL(request) + URLHelper.getCurrentUrl(request);
%>

<kantega:section id="bodyclass">error</kantega:section>

<kantega:section id="body">
    <div id="errorWrapper">
        <img src="${pageContext.request.contextPath}/META-INF/resources/WEB-INF/jsp/admin/administration/admin/bitmaps/default/framework/error.png" alt="">
        <div class="dialogBox">
            <h1>
                <kantega:label key="aksess.error.error"/>
            </h1>
            <div class="body">
                <span class="label"><kantega:label key="aksess.error.label"/></span> <%=error%>

                <form action="http://opensource.kantega.no/aksess/feedback/error" target="_new" method="post" name="errorreport">
                    <input type="hidden" name="product" value="Aksess">
                    <input type="hidden" name="timestamp" value="<%=timestamp%>">
                    <input type="hidden" name="error" value="<%=error%>">
                    <input type="hidden" name="details" value="<%=details%>">
                    <input type="hidden" name="ident" value="<%=ident%>">
                    <input type="hidden" name="email" value="<%=email%>">
                    <input type="hidden" name="applicationUrl" value="<%=url%>">
                    <input type="hidden" name="site" value="<%=request.getServerName()%>">
                    <input type="hidden" name="version" value="<%=Aksess.getVersion()%>">
                    <input type="hidden" name="release" value="<%= Aksess.getBuildRevision() %>">
                    <input type="submit" value="<kantega:label key="aksess.error.sendreport"/>">
                </form>
                <p class="goBack">
                    <a class="goBack" href="Javascript:history.back()"><kantega:label key="aksess.error.back"/></a>
                </p>
            </div>
        </div>
        <div class="dialogBoxArrow"></div>

    </div>
</kantega:section>

<%@ include file="../../WEB-INF/jsp/admin/layout/loginLayout.jsp" %>
