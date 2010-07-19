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

import no.kantega.commons.util.LocaleLabels;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.ServletException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 *
 */
public class CalendarTag extends TagSupport {
    private String key = null;
    private String url;
    private String date;
    private String dateFormat = "yyyy-MM-dd";
    private String parameter = "date";
    private String resolverattr;
    public static final String SELECTED_KEY = "SelectedKey";
    public static final String URL_KEY = "LinkKey";
    public static final String PARAMETER_KEY = "DateParameterKey";
    public static final String RESOLVER_KEY = "EventResolverKey";

    public void setKey(String key) {
        this.key = key;
    }

    public int doStartTag() throws JspException {
        if(date != null) {
            try {
                Date d = new SimpleDateFormat(dateFormat).parse(date);
                pageContext.getRequest().setAttribute(SELECTED_KEY, d);
            } catch (ParseException e) {
                // Bruker default dato
            }
        } else {
             pageContext.getRequest().setAttribute(SELECTED_KEY, new Date());
        }

        try {
            pageContext.getRequest().setAttribute(URL_KEY, url);
            pageContext.getRequest().setAttribute(PARAMETER_KEY, parameter);

            pageContext.getRequest().setAttribute(RESOLVER_KEY, resolverattr);
            pageContext.include("/WEB-INF/jsp/taglibs/util/calendar.jsp");
        } catch (IOException e) {
            throw new JspException("ERROR: CalendarTag:" + e);
        } catch (ServletException e) {
            throw new JspException("ERROR: CalendarTag:" + e);
        }
        return SKIP_BODY;
    }


    public int doEndTag() throws JspException {
         return TagSupport.EVAL_PAGE;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public void setResolverattr(String resolverattr) {
        this.resolverattr = resolverattr;
    }
}
