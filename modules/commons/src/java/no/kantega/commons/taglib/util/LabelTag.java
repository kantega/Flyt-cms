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
import org.springframework.web.util.JavaScriptUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Print the value of the property string with the given key.
 * If the String attribute locale is set this locale will be used, else
 * the request scoped attribute aksess_locale is tried.
 * If the request attribute is absent NO_no is used.
 */
public class LabelTag extends TagSupport implements DynamicAttributes {
    private String key = null;
    private String bundle = LocaleLabels.DEFAULT_BUNDLE;
    private String locale = null;
    private boolean escapeJavascript = false;
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
                Locale locale;
                if (isNotBlank(this.locale)){
                    String[] localePair = this.locale.split("_");
                    locale = new Locale(localePair[0], localePair[1]);
                } else {
                    locale = LocaleLabels.getLocaleFromRequestOrDefault(request);
                }

                String textLabel = "";
                if (bundle.equals(LocaleLabels.DEFAULT_BUNDLE)) {
                    textLabel = LocaleLabels.getLabel(key, "site", locale, params);
                }
                if (!bundle.equals(LocaleLabels.DEFAULT_BUNDLE) || textLabel.equals(key)) {
                    textLabel = LocaleLabels.getLabel(key, bundle, locale, params);
                }
                if (escapeJavascript) {
                    textLabel = JavaScriptUtils.javaScriptEscape(textLabel);
                }
                out.print(textLabel);
            } else {
                out.print("ERROR: LabelTag, missing key " + key);
            }
            params = null;
            escapeJavascript = false;
            key = null;
            bundle = LocaleLabels.DEFAULT_BUNDLE;
            locale = null;
        } catch (IOException e) {
            throw new JspException("ERROR: LabelTag:" + e.getMessage(), e);
        }

        return SKIP_BODY;
    }


    public int doEndTag() throws JspException {
         return EVAL_PAGE;
    }

    public void setDynamicAttribute(String uri, String localname, Object o) throws JspException {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(localname, o);
    }

    public void setEscapeJavascript(boolean escapeJavascript) {
        this.escapeJavascript = escapeJavascript;
    }
}
