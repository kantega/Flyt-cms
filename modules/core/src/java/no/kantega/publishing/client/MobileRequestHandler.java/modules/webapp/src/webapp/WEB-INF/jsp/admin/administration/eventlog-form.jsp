<%@ page import="no.kantega.publishing.common.data.enums.ContentProperty" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="no.kantega.publishing.common.data.EventLogEntry" %>
<%@ page import="no.kantega.publishing.common.data.enums.ObjectType" %>
<%@ page import="no.kantega.publishing.common.data.enums.Event" %>
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
    <script type="text/javascript">
        $(function() {
            $("#from_date").datepicker();
            $("#end_date").datepicker();
        });
    </script>
    <form name="eventlog" action="SearchEventLog.action" method="post" class="inline">

        <admin:box>
            <h1><kantega:label key="aksess.eventlog.title"/></h1>
            <div class="formElement">
                <div class="heading">
                    <kantega:label key="aksess.eventlog.period"/>
                </div>
                <div class="inputs">
                    <label for="from_date"><kantega:label key="aksess.publishinfo.period.from"/></label>
                    <input type="text" id="from_date" name="from_date" size="10" maxlength="10" value="<admin:formatdate date="${fromDate}"/>">
                    <label for="end_date"><kantega:label key="aksess.publishinfo.period.until"/></label>
                    <input type="text" id="end_date" name="end_date" size="10" maxlength="10" value="">
                </div>
            </div>
            <div class="formElement">
                <div class="heading">
                    <kantega:label key="aksess.eventlog.userid"/>
                </div>
                <div class="inputs">
                    <input type="text" name="userid" size="20" maxlength="20" value="">
                </div>
            </div>
            <div class="formElement">
                <div class="heading">
                    <kantega:label key="aksess.eventlog.object"/>
                </div>
                <div class="inputs">
                    <input type="text" name="subject" size="20" maxlength="20" value="">
                </div>
            </div>
            <div class="formElement">
                <div class="heading">
                    <kantega:label key="aksess.eventlog.event"/>
                </div>
                <div class="inputs">
                    <select name="event">
                        <option value=""></option>
                        <%
                            for (int i = 0; i < Event.ALL_EVENTS.length; i++) {
                                String key = "aksess.event." + Event.ALL_EVENTS[i];
                        %>
                        <option value="<%=Event.ALL_EVENTS[i]%>"><kantega:label key="<%=key%>"/></option>
                        <%
                            }
                        %>
                    </select>
                </div>
            </div>

            <div class="ui-state-highlight">
                <kantega:label key="aksess.eventlog.help"/> <%=Aksess.getEventLogMaxAge()%> <kantega:label key="aksess.eventlog.help2"/>
            </div>

            <div class="buttonGroup">
                <span class="button"><input type="submit" class="search" value="<kantega:label key="aksess.button.search"/>"></span>
            </div>

        </admin:box>
    </form>

</kantega:section>
<%@ include file="../layout/administrationLayout.jsp" %>
