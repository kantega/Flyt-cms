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

package no.kantega.publishing.api.taglibs.util;

import no.kantega.commons.configuration.Configuration;
import no.kantega.commons.exception.ConfigurationException;
import no.kantega.publishing.common.Aksess;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

/**
 * User: Anders Skar, Kantega AS
 * Date: Mar 12, 2008
 * Time: 10:28:19 AM
 */
public class GetConfigTag  extends TagSupport {
    String key = null;
    String defaultValue = "";

    public void setKey(String key) {
        this.key = key;
    }

    public void setDefault(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public int doStartTag() throws JspException {
        JspWriter out;
        try {
            out = pageContext.getOut();

            Configuration config = Aksess.getConfiguration();

            out.write(config.getString(key, defaultValue));
        } catch (ConfigurationException e) {
            throw new JspException("ERROR: GetConfigTag", e);
        } catch (IOException e) {
            throw new JspException("ERROR: GetConfigTag", e);
        }

        key = null;
        defaultValue = "";

        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
         return EVAL_PAGE;
    }
}