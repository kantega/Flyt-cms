<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ page import="java.util.Calendar,
                 java.util.GregorianCalendar,
                 java.util.Date,
                 no.kantega.commons.client.util.RequestParameters"%>
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
    RequestParameters param = new RequestParameters(request, "utf-8");

    int offset = param.getInt("offset");
    GregorianCalendar cal = (GregorianCalendar)GregorianCalendar.getInstance();
    cal.setTime(new Date());

    if (offset != -1) {
        cal.add(Calendar.MONTH, offset);
    } else {
        offset = 0;
    }

    int selectedDay = cal.get(Calendar.DAY_OF_MONTH);

    cal.setFirstDayOfWeek(Calendar.MONDAY);
    int month = cal.get(Calendar.MONTH);
    int year = cal.get(Calendar.YEAR);

    cal.set(Calendar.DAY_OF_MONTH, 1);
    int skipdays = cal.get(Calendar.DAY_OF_WEEK) -1;
    if(skipdays == 0) {
        skipdays = 6;
    } else {
        skipdays--;
    }

    int dom[] = {31, 28, 31, 30, /* jan feb mar apr */
                 31, 30, 31, 31, /* may jun jul aug */
                 30, 31, 30, 31  /* sep oct nov dec */
    };

    int daysInMonth = dom[month];
    if(cal.isLeapYear(year) && month == 1) {
        daysInMonth++;
    }

%>
<html>
<head>
	<title><kantega:label key="aksess.calendar.title"/></title>
    <link rel="stylesheet" type="text/css" href="../css/<%=skin%>.css">
</head>
<script language="Javascript" src="../js/browserdetect.js"></script>
<script language="Javascript" src="../js/edit.jsp"></script>
<script language="Javascript">
    function selectDay(year, month, day) {
        if (day < 10) {
            day = "0" + day;
        }
        if (month < 10) {
            month = "0" + month;
        }
        if (window.opener) {
            window.opener.insertValueIntoForm("" + day + "." + month + "." + year);
        }
        window.close();
    }
</script>
<body class="bodyWithMargin">
<table border="0" width="260" cellspacing="0" cellpadding="0">
    <tr>
        <td width="300"><img src="../bitmaps/blank.gif" width="260" height="1"></td>
    </tr>
    <tr>
        <td>
            <table border="0" cellspacing="0" cellpadding="0" width="100%">
                <%
                        String label = "aksess.calendar.month" + month;
                    %>
                        <tr>
                            <td class="tableHeading" width="30"><a href="calendar.jsp?offset=<%=(offset-1)%>">&lt;&lt;</a></td>
                            <td class="tableHeading" width="200" align="center"><b><kantega:label key="<%=label%>"/> - <%=year%></b></td>
                            <td class="tableHeading" width="30" align="right"><a href="calendar.jsp?offset=<%=(offset+1)%>">&gt;&gt;</a></td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr>
                <td><img src="../bitmaps/blank.gif" width="2" height="4"></td>
            </tr>
            <tr>
                <td>
                    <table border="0" cellspacing="0" cellpadding="0" width="100%">
                    <tr>
                        <td><b>M</b></td>
                        <td><b>T</b></td>
                        <td><b>O</b></td>
                        <td><b>T</b></td>
                        <td><b>F</b></td>
                        <td><b>L</b></td>
                        <td><b>S</b></td>
                    </tr>
                    <tr>
                    <%
                        for(int i = 0; i < skipdays; i++) {
                           %><td></td><%
                        }


                        for(int i = 1; i <= daysInMonth; i++) {
                            cal.set(Calendar.DAY_OF_MONTH, i);
                            String clazz = "calendarDay";
                            if(i == selectedDay) {
                                clazz =  "calendarToday";
                            }
                            %><td><div class="<%=clazz%>"><a href="Javascript:selectDay(<%=year%>, <%=(month+1)%>,<%=i%>)"><%=i%></a></div></td><%
                            if((i+skipdays) % 7== 0) {
                                out.println("</tr><tr>");
                            }
                        }
                    %>
                    </tr>
                    </table>
                </td>
            </tr>
        </table>
</body>
</html>
<%@ include file="../include/jsp_footer.jsf" %>