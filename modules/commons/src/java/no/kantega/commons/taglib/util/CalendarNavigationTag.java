/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.commons.taglib.util;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class CalendarNavigationTag extends BodyTagSupport {

    private String dir ="next";
    private static final String NEXT = "next";


    public int doStartTag() throws JspException {
        return EVAL_BODY_TAG;
    }

    public int doAfterBody() throws JspException {
        HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

        Date date  = (Date) request.getAttribute(CalendarTag.SELECTED_KEY);

        String url  = (String) request.getAttribute(CalendarTag.URL_KEY);

        String parameter  = (String) request.getAttribute(CalendarTag.PARAMETER_KEY);

        GregorianCalendar cal = (GregorianCalendar)GregorianCalendar.getInstance();

        cal.setTime(date);

        int selectedDay = cal.get(Calendar.DAY_OF_MONTH);

        cal.setFirstDayOfWeek(Calendar.MONDAY);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);


        int dom[] = {31, 28, 31, 30, /* jan feb mar apr */
                     31, 30, 31, 31, /* may jun jul aug */
                     30, 31, 30, 31  /* sep oct nov dec */
        };

        int nextMonth = (month == 11) ? 0 : month +1;
        int prevMonth = (month == 0) ? 11 : month -1;
        int prevYear = (month == 0) ? year-1 : year;
        int nextYear = (month == 11) ? year +1 : year;
        int prevDay = (selectedDay < dom[prevMonth]) ? selectedDay : dom[prevMonth];
        int nextDay = (selectedDay < dom[nextMonth]) ? selectedDay : dom[nextMonth];

        String nextDate = nextYear + "-" + (nextMonth+1) +"-" +prevDay;
        String prevDate = prevYear + "-" + (prevMonth+1) +"-" +nextDay;

        JspWriter out = getPreviousOut();

        if (url.indexOf("?") == -1) {
            url += "?";            
        }

        String language = (String) request.getAttribute("language");
        if (language != null) {
            url += "&amp;language=" + language;
        }
        try {
            out.write("<a href=\"" +url + "&amp;" + parameter + "=" + (dir.equals(NEXT) ? nextDate : prevDate) +"\">");
            bodyContent.writeOut(out);
            out.write("</a>");
        } catch (IOException e) {
            throw new JspException(e);
        } finally {
            bodyContent.clearBody();
        }

        return SKIP_BODY;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }
}
