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

package no.kantega.publishing.controls.standard;

import no.kantega.commons.client.util.RequestParameters;
import no.kantega.publishing.api.content.Language;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.controls.AksessController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Author: Kristian Lier Selnæs, Kantega AS
 * Date: 19.des.2006
 * Time: 10:46:45
 */
public class NewsArchiveController implements AksessController {


    private int startYear = -1;
    private int defaultMax = 20;
    private String description = "Nyheter - Brukes for å vise liste med nyheter der brukeren velger dato/år";

    public Map handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Calendar calendar = new GregorianCalendar();
        RequestParameters param = new RequestParameters(request);
        Content c = (Content)request.getAttribute("aksess_this");

        String keyword = param.getString("keyword");

        Date fromDate = null;
        Date toDate   = null;

        int year  = -1;
        int month = -1;

        int max = -1;
        String showArchived = (c.getAttributeValue("vis arkiverte") != null)? c.getAttributeValue("vis arkiverte") : "false";

        year  = param.getInt("year");
        month = param.getInt("month");

        int currentYear = calendar.get(Calendar.YEAR);
        if (month != -1 && year == -1) {
            year = currentYear;
        }

        if (keyword == null || keyword.length() == 0) {
            if (year == -1) {
                max = defaultMax;

                if (c != null) {
                    String userMax = c.getAttributeValue("max");
                    if (userMax != null && userMax.length() > 0) {
                        max = Integer.parseInt(userMax);
                    }
                }
                
            } else {
                showArchived = "true";

                Calendar cal = new GregorianCalendar();

                cal.set(Calendar.DATE, 1);

                if (month == -1) {
                    cal.set(Calendar.MONTH, 0);
                    cal.set(Calendar.YEAR, year);
                    fromDate = cal.getTime();

                    cal.add(Calendar.YEAR, 1);
                    toDate = cal.getTime();
                } else {
                    cal.set(Calendar.MONTH, month);
                    cal.set(Calendar.YEAR, year);
                    fromDate = cal.getTime();

                    cal.add(Calendar.MONTH, 1);
                    toDate = cal.getTime();
                }
            }
            keyword = "";
        } else {
            showArchived = "true";
        }

        List yearList = new ArrayList();
        if(startYear == -1){
            startYear = currentYear;
        }
        for (int i = currentYear; i >= startYear; i--) {
            yearList.add(i);
        }

        Map monthMap = new TreeMap();
        Locale locale = new Locale("no", "NO");
        Content content = (Content) request.getAttribute("aksess_this");
        if (content != null) {
            locale = Language.getLanguageAsLocale(content.getLanguage());
        }
        DateFormat df = new SimpleDateFormat("MMMMM", locale);
        for (int i = 0; i < 12; i++) {
            calendar.set(year, i, 1);
            String m = df.format(calendar.getTime());
            monthMap.put(i,m);
        }


        Map model = new HashMap();
        model.put("fromDate", fromDate);
        model.put("toDate", toDate);
        model.put("showArchived", showArchived);
        model.put("max", max);
        model.put("yearList", yearList);
        model.put("monthMap", monthMap);
        model.put("selectedYear", year);
        model.put("selectedMonth", month);


        return model;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }


    public void setDefaultMax(int defaultMax) {
        this.defaultMax = defaultMax;
    }
}
