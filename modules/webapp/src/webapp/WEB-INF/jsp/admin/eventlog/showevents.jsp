<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="java.text.DateFormat,
                 java.text.SimpleDateFormat,
                 no.kantega.commons.client.util.RequestParameters"%>
<%@ include file="../../../../admin/include/jsp_header.jsf" %>
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
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
	<title>searchlog.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<body class="bodyWithMargin">
  <table border="0" cellspacing="0" cellpadding="2">
    <tr class="tableHeading">
      <td><strong><kantega:label key="aksess.eventlog.tidspunkt"/></strong></td>
      <td><strong><kantega:label key="aksess.eventlog.hendelse"/></strong></td>
      <td><strong><kantega:label key="aksess.eventlog.objekt"/></strong></td>
      <td><strong><kantega:label key="aksess.eventlog.brukerid"/></strong></td>
      <td><strong><kantega:label key="aksess.eventlog.remoteaddr"/></strong></td>
    </tr>
    <%
        List events = (List)request.getAttribute("events");
        DateFormat df = new SimpleDateFormat(Aksess.getDefaultDateFormat());
        DateFormat tf = new SimpleDateFormat(Aksess.getDefaultTimeFormat());
        for (int i = 0; i < events.size(); i++) {
            EventLogEntry event = (EventLogEntry)events.get(i);
            String date = df.format(event.getTime()) + "-" + tf.format(event.getTime());
            String link = null;
            int subjectId = event.getSubjectId();
            int subjectType = event.getSubjectType();
            if (subjectId > 0) {
                if (subjectType == ObjectType.CONTENT) {
                    link = Aksess.getContextPath() + "/content.ap?contentId=" + subjectId;
                } else if (subjectType == ObjectType.MULTIMEDIA) {
                    link = Aksess.getContextPath() + "/multimedia.ap?id=" + subjectId;
                }
            }
    %>
        <tr class="tableRow<%=(i%2)%>">
          <td><%=date%></td>
          <td><%=event.getEventName()%></td>
          <td>
              <%
                  if (link != null) {
              %>
              <a href="<%=link%>" target="contentinfo">
              <%
                  }
              %>
              <%=event.getSubjectName()%>
              <%
                  if (link != null) {
              %>
              </a>
              <%
                  }
              %>
          </td>
          <td><%=event.getUserId()%></td>
          <td><a href="http://www.ratite.com/whois/whois.cgi?domain=<%=event.getRemoteAddress()%>" target="ipinfo"><%=event.getRemoteAddress()%></a></td>
        </tr>
    <%
        }
    %>
   </table>
   <%
       if (events.size() == 0) {
   %>
   <p><strong><kantega:label key="aksess.eventlog.ingentreff"/></strong></p>
   <%
       }
   %>
</body>
</html>
<%@ include file="../../../../admin/include/jsp_footer.jsf" %>