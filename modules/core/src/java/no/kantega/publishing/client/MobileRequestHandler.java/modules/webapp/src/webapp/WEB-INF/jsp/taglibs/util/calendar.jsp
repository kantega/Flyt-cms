<%@ page import="java.util.Calendar,
                 java.util.GregorianCalendar,
                 java.util.Date,
                 no.kantega.commons.taglib.util.CalendarTag,
                 java.text.SimpleDateFormat,
                 no.kantega.commons.taglib.util.CalendarDayHasEventResolver,
                 no.kantega.publishing.common.service.ContentManagementService,
                 no.kantega.publishing.common.data.Content"%>
<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
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
    int language = 0;
    String currentId = "";
    Content currentPage = (Content)request.getAttribute("aksess_this");

    if (request.getParameter("language") != null) {
        language = Integer.parseInt(request.getParameter("language"));
    }  else {
        if (request.getAttribute("language") != null) {
            language = Integer.parseInt((String)request.getAttribute("language"));
        } else {
            if (currentPage != null) {
                language = currentPage.getLanguage();
            }
        }
    }

    if (currentPage != null) {
        currentId = String.valueOf(currentPage.getId());
    }

    String url = (String) request.getAttribute(CalendarTag.URL_KEY);

    String parameter = (String) request.getAttribute(CalendarTag.PARAMETER_KEY);

    CalendarDayHasEventResolver resolver = (CalendarDayHasEventResolver) request.getAttribute((String)request.getAttribute(CalendarTag.RESOLVER_KEY));

    GregorianCalendar cal = (GregorianCalendar)GregorianCalendar.getInstance();

    Date date = (Date) request.getAttribute(CalendarTag.SELECTED_KEY);
    if(date != null) {
        cal.setTime(date);
    } else {
        cal.setTime(new Date());
    }
    int selectedDay = cal.get(Calendar.DAY_OF_MONTH);

    cal.setFirstDayOfWeek(Calendar.MONDAY);
    int month = cal.get(Calendar.MONTH);
    int year = cal.get(Calendar.YEAR);

    cal.set(Calendar.DAY_OF_MONTH, 1);
    int skipdays = cal.get(Calendar.DAY_OF_WEEK) -1;
    if(skipdays == 0) {
        skipdays = 6;
    }else {
        skipdays--;
    }
    String[] months = {"Januar", "Februar", "Mars",
                       "April", "Mai", "Juni",
                       "Juli", "August", "September", "Oktober",
                       "November", "Desember"};

    String[] monthsEn = {"January", "February", "March",
                         "April", "May", "June",
                         "July", "August", "September", "October",
                         "November", "December"};


    int dom[] = {31, 28, 31, 30, /* jan feb mar apr */
                 31, 30, 31, 31, /* may jun jul aug */
                 30, 31, 30, 31  /* sep oct nov dec */
    };

    int daysInMonth = dom[month];
    if(cal.isLeapYear(year) && month == 1) {
        daysInMonth++;
    }


%>
        <table border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td colspan="7" align="center">
                    <div class="monthheader"><%=((language==3) ? monthsEn[month] : months[month]) +" " +year%></div>
                    </td>
                </tr>
                <tr class="weekrow">
                    <td align="center">M</td>
                    <td align="center">T</td>
                    <td align="center">O</td>
                    <td align="center">T</td>
                    <td align="center">F</td>
                    <td align="center">L</td>
                    <td align="center">S</td>
                </tr>
                <tr>
                    <%
                        for(int i = 0; i < skipdays; i++) {
                           %><td></td><%
                        }


                        for(int i = 1; i <= daysInMonth; i++) {
                            cal.set(Calendar.DAY_OF_MONTH, i);
                            String clazz = "day";
                            if(resolver != null && resolver.hasEvent(cal.getTime())) {
                                clazz= "daywithevent";
                            }
                            if(i == selectedDay) {
                                clazz =  "selectedday";
                            }

                            String navUrl = url;
                            if (navUrl.indexOf("?") == -1) {
                                navUrl += "?";
                            } else {
                                navUrl += "&amp;";
                            }
                            navUrl += parameter +"=" +year +"-" +(month+1) +"-" + i;
                            %><td><div class="<%=clazz%>"><a href="<%=navUrl%>"><%=i%></a></div></td><%
                            if((i+skipdays) % 7== 0) {
                                out.println("</tr><tr>");
                            }
                        }
                    %>
                    <td></td>
                </tr>
            </table>
