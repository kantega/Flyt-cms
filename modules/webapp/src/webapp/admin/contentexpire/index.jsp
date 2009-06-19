<%@ page import="no.kantega.commons.client.util.RequestParameters" %>
<%@ page import="no.kantega.publishing.common.Aksess" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.GregorianCalendar" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/aksess" prefix="aksess" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="../include/jsp_header.jsf" %>
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
    RequestParameters param = new RequestParameters(request);
    DateFormat df = new SimpleDateFormat(Aksess.getDefaultDateFormat());

    Date from = param.getDate("from_date", Aksess.getDefaultDateFormat());
    Date end  = param.getDate("end_date", Aksess.getDefaultDateFormat());

    if (from == null) {
        from = new Date();
    }

    if (end == null) {
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DATE, 30);
        end = cal.getTime();
    }

    pageContext.setAttribute("expireFromDateStr", df.format(from));
    pageContext.setAttribute("expireToDateStr", df.format(end));

    pageContext.setAttribute("expireFromDate", from);
    pageContext.setAttribute("expireToDate", end);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<title>contentexpire/index.jsp</title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
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

    function showExpired() {
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
<body class="bodyWithMargin">
    <form name="myform" action="index.jsp" method="post">
    <table border="0" cellspacing="0" cellpadding="0" width="600">
        <tr>
            <td class="tableHeading"><b><kantega:label key="aksess.contentexpire.period"/></b></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="2"></td>
        </tr>
        <tr>
            <td>
                <table border="0" cellspacing="0" cellpadding="0" width="470">
                    <tr>
                        <td width="120"><kantega:label key="aksess.contentexpire.period.from"/></td>
                        <td width="150">
                            <table border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td><input type="text" id="from_date" name="from_date" size="10" maxlength="10" value="<c:out value="${expireFromDateStr}"/>"></td>
                                    <td><a href="#" id="velgdatofrom_date0"><img src="../bitmaps/common/buttons/mini_velg.gif" border="0" alt=""></a></td>
                                    <td><a href="#" id="velgdatofrom_date1" class="button"><kantega:label key="aksess.button.velg"/></a></td>
                                </tr>
                            </table>
                        </td>
                        <td width="50"><kantega:label key="aksess.contentexpire.period.end"/></td>
                        <td width="150">
                            <table border="0" cellspacing="0" cellpadding="0">
                                <tr>
                                    <td><input type="text" id="end_date" name="end_date" size="10" maxlength="10" value="<c:out value="${expireToDateStr}"/>"></td>
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
            <td align="right"><a href="Javascript:showExpired()"><img src="../bitmaps/<%=skin%>/buttons/ok.gif" border="0"></a></td>
        </tr>
        <tr>
            <td><img src="../bitmaps/blank.gif" width="2" height="16"></td>
        </tr>
        <tr>
            <td>
                <table border="0" cellspacing="0" cellpadding="0" width="100%">
                    <tr class="tableHeading">
                        <td><kantega:label key="aksess.contentexpire.date"/></td>
                        <td><kantega:label key="aksess.contentexpire.page"/></td>
                    </tr>
                    <%
                        int i = 0;
                    %>
                    <aksess:getcollection findall="true" name="pages" skipattributes="true" showexpired="true" expirefromdate="${expireFromDate}" expiretodate="${expireToDate}" orderby="expiredate">
                        <tr class="tableRow<%=(i%2)%>">
                            <td><aksess:getattribute name="expiredate" collection="pages"/></td>
                            <td><aksess:link collection="pages" target="_new"><aksess:getattribute name="title" collection="pages"/></aksess:link></td>
                        </tr>
                        <%
                            i++;
                        %>
                    </aksess:getcollection>
                    <tr>
                        <td colspan="2"><br>
                            <table border="0" cellspacing="0" cellpadding="0" class="info">
                                <tr>
                                    <td>
                                        <kantega:label key="aksess.contentexpire.help"/>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
    </form>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>
