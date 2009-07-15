<%@ page import="no.kantega.publishing.common.data.enums.ContentProperty" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="no.kantega.publishing.common.data.EventLogEntry" %>
<%@ page import="no.kantega.publishing.common.data.enums.ObjectType" %>
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
    <div class="fieldset">
        <fieldset>
            <legend><kantega:label key="aksess.eventlog.title"/></legend>
            <table>
                <tr>
                    <th><strong><kantega:label key="aksess.eventlog.datetime"/></strong></th>
                    <th><strong><kantega:label key="aksess.eventlog.event"/></strong></th>
                    <th><strong><kantega:label key="aksess.eventlog.object"/></strong></th>
                    <th><strong><kantega:label key="aksess.eventlog.userid"/></strong></th>
                    <th><strong><kantega:label key="aksess.eventlog.remoteaddr"/></strong></th>
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
                        String eventName = "aksess.event." + event.getEventName();
                %>
                <tr class="tableRow<%=(i%2)%>">
                    <td><%=date%></td>
                    <td><kantega:label key="<%=eventName%>"/></td>
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
            <div class="helpText"><kantega:label key="aksess.eventlog.nohits"/></div>
            <%
                }
            %>
        </fieldset>
    </div>
</kantega:section>
<%@ include file="../layout/administrationLayout.jsp" %>
