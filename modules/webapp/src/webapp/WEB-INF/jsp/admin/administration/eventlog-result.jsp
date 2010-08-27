<%@ page import="no.kantega.publishing.common.data.enums.ContentProperty" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="no.kantega.publishing.common.data.EventLogEntry" %>
<%@ page import="no.kantega.publishing.common.data.enums.ObjectType" %>
<%@ page import="no.kantega.publishing.common.util.PrettyURLEncoder" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
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
<kantega:section id="title">
    <kantega:label key="aksess.eventlog.title"/>
</kantega:section>

<kantega:section id="content">
    <admin:box>
        <h1><kantega:label key="aksess.eventlog.title"/></h1>
        <table class="fullWidth dataTable">
            <thead>
            <tr>
                <th><kantega:label key="aksess.eventlog.datetime"/></th>
                <th><kantega:label key="aksess.eventlog.event"/></th>
                <th><kantega:label key="aksess.eventlog.object"/></th>
                <th><kantega:label key="aksess.eventlog.userid"/></th>
                <th><kantega:label key="aksess.eventlog.remoteaddr"/></th>
            </tr>
            </thead>
            <tbody>
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
                            link = Aksess.getContextPath() + PrettyURLEncoder.createContentUrl(subjectId, event.getSubjectName(), null);
                        } else if (subjectType == ObjectType.MULTIMEDIA) {
                            link = Aksess.getContextPath() + "/multimedia.ap?id=" + subjectId;
                        }
                    }

                    String eventInfo = "";
                    String eventName = event.getEventName();
                    if (eventName.indexOf(":") != -1) {
                        // If eventname contains :, the text after : is additional info
                        eventInfo = ": " + eventName.substring(eventName.indexOf(":"), eventName.length());
                        eventName = eventName.substring(0, eventName.indexOf(":"));
                    }
                    eventName = "aksess.event." + eventName;
            %>
            <tr class="tableRow<%=(i%2)%>">
                <td><%=date%></td>
                <td><kantega:label key="<%=eventName%>"/><%=eventInfo%></td>
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
            </tbody>
        </table>
        <%
            if (events.size() == 0) {
        %>
        <div class="ui-state-highlight"><kantega:label key="aksess.eventlog.nohits"/></div>
        <%
            }
        %>
    </admin:box>
</kantega:section>
<%@ include file="../layout/administrationLayout.jsp" %>
