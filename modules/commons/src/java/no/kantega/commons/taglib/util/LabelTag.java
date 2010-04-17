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
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

/**
 *
 */
public class LabelTag extends TagSupport implements DynamicAttributes {
    private String key = null;
    private String bundle = LocaleLabels.DEFAULT_BUNDLE;
    private String locale = null;
    private Map<String, Object> params = null;

    public void setKey(String key) {
        this.key = key;
    }

    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public int doStartTag() throws JspException {
        JspWriter out;
        try {
            out = pageContext.getOut();
            HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
            if (key != null) {
                Locale locale = (Locale)request.getAttribute("aksess_locale");
                if( this.locale!=null){
                    String[] localePair = this.locale.split("_");
                    locale = new Locale(localePair[0],localePair[1]);
                }
                if (locale == null) {
                    locale = new Locale("no", "NO");
                }

                String textLabel = LocaleLabels.getLabel(key, bundle, locale, params);
                out.print(textLabel);
            } else {
                out.print("ERROR: LabelTag, missing key");
            }
            params = null;
        } catch (IOException e) {
            throw new JspException("ERROR: LabelTag:" + e);
        }

        return SKIP_BODY;
    }


    public int doEndTag() throws JspException {
         return EVAL_PAGE;
    }

    public void setDynamicAttribute(String uri, String localname, Object o) throws JspException {
        if (params == null) {
            params = new HashMap<String, Object>();
        }
        params.put(localname, o);
    }
}
