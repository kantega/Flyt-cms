<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="no.kantega.publishing.common.data.enums.Event" %>
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

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
	<title>Søk i hendelseslogg</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
    <link rel="stylesheet" type="text/css" href="../css/calendar-<%=skin%>.css">
</head>
<script type="text/javascript" src="../js/validation.js"></script>
<script type="text/javascript" src="../js/date.jsp"></script>
<script type="text/javascript" src="../js/calendar/calendar.js"></script>
<script type="text/javascript" src="../js/calendar/calendar-en.js"></script>
<script type="text/javascript" src="../js/calendar/calendar-no.js" charset="utf-8"></script>
<script type="text/javascript" src="../js/calendar/calendar-setup.js"></script>

<script language="Javascript">
    var isSortOrderModified = false

    function initialize() {
        try {
            // document.myform.elements[0].focus()
        } catch (e) {
            // Usynlig element som ikke kan få fokus
        }
    }

    function searchLog() {
        var form = document.myform;

        // Reset validationErrors
        validationErrors.length = 0;

        validateDate(form.from_date, false, false);
        validateDate(form.end_date, false, false);

        if (showValidationErrors()) {
            form.submit();
        }

    }
</script>


<body onload="initialize()" class="bodyWithMargin">
<%@ include file="../../../../admin/include/infobox.jsp" %>
<form name="myform" action="SearchEventLog.action" method="post">
    <table border="0" cellspacing="0" cellpadding="0" width="600">
        <tr>
            <td class="tableHeading"><b><kantega:label key="aksess.eventlog.periode"/></b></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2"></td>
        </tr>
        <tr>
            <td>
                <table border="0" cellspacing="0" cellpadding="0" width="470">
                    <tr>
                        <td width="120"><kantega:label key="aksess.eventlog.periode.fra"/></td>
                        <td width="150">
                            <table border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td><input type="text" id="from_date" name="from_date" size="10" maxlength="10" value="${fromDate}"></td>
                                    <td><a href="#" id="velgdatofrom_date0"><img src="../bitmaps/common/buttons/mini_velg.gif" border="0" alt=""></a></td>
                                    <td><a href="#" id="velgdatofrom_date1" class="button"><kantega:label key="aksess.button.velg"/></a></td>
                                </tr>
                            </table>
                        </td>
                        <td width="50"><kantega:label key="aksess.eventlog.periode.til"/></td>
                        <td width="150">
                            <table border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td><input type="text" id="end_date" name="end_date" size="10" maxlength="10"></td>
                                    <td><a href="#" id="velgdatoend_date0"><img src="../bitmaps/common/buttons/mini_velg.gif" border="0" alt=""></a></td>
                                    <td><a href="#" id="velgdatoend_date1" class="button"><kantega:label key="aksess.button.velg"/></a></td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
                <script type="text/javascript">
                    Calendar.setup( { inputField  : "from_date", ifFormat : "%d.%m.%Y", button : "velgdatofrom_date0", firstDay: 1 } );
                    Calendar.setup( { inputField  : "from_date", ifFormat : "%d.%m.%Y", button : "velgdatofrom_date1", firstDay: 1 } );
                    Calendar.setup( { inputField  : "end_date", ifFormat : "%d.%m.%Y", button : "velgdatoend_date0", firstDay: 1 } );
                    Calendar.setup( { inputField  : "end_date", ifFormat : "%d.%m.%Y", button : "velgdatoend_date1", firstday: 1 } );
                </script>
            </td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="16"></td>
        </tr>
        <tr>
            <td class="tableHeading"><b><kantega:label key="aksess.eventlog.kriterier"/></b></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2"></td>
        </tr>
        <tr>
            <td>
                <table border="0" cellspacing="0" cellpadding="0" width="470">
                    <tr>
                        <td width="120"><kantega:label key="aksess.eventlog.brukerid"/></td>
                        <td width="350"><input type="text" name="userid" size="20" maxlength="20" value="" style="width:300px;"></td>
                    </tr>
                    <tr>
                        <td><kantega:label key="aksess.eventlog.objekt"/></td>
                        <td><input type="text" name="subject" size="20" maxlength="20" value="" style="width:300px;"></td>
                    </tr>
                    <tr>
                        <td><kantega:label key="aksess.eventlog.hendelse"/></td>
                        <td>
                            <select name="event" style="width:300px;">
                                <option value=""></option>
                                <%
                                    for (int i = 0; i < Event.ALL_EVENTS.length; i++) {
                                %>
                                <option value="<%=Event.ALL_EVENTS[i]%>"><%=Event.ALL_EVENTS[i]%></option>
                                <%
                                    }
                                %>
                            </select>
                            </td>
                    </tr>
                    <tr>
                        <td colspan="2">&nbsp;</td>
                    </tr>
                    <tr>
                        <td colspan="2"><a href="Javascript:searchLog()"><img src="../bitmaps/<%=skin%>/buttons/sok.gif" border="0"></a></td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="16"></td>
        </tr>
        <tr>
            <td>
                <div class=helpText>
                <kantega:label key="aksess.eventlog.hjelp"/> <%=Aksess.getEventLogMaxAge()%> <kantega:label key="aksess.eventlog.hjelp2"/>
                </div>
            </td>
        </tr>
    </table>
</form>
</body>
</html>
<%@ include file="../../../../admin/include/jsp_footer.jsf" %>
