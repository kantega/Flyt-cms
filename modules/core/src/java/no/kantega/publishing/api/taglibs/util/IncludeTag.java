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

import no.kantega.commons.exception.NotAuthorizedException;
import no.kantega.commons.exception.SystemException;
import no.kantega.publishing.api.taglibs.content.util.AttributeTagHelper;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

public class IncludeTag  extends TagSupport {
    String url = null;

    public void setUrl(String url) {
        this.url = url;
    }

    public int doStartTag() throws JspException {
        if (url != null) {
            try {
                url = AttributeTagHelper.replaceMacros(url, pageContext);
                pageContext.include(url);
            } catch (ServletException e) {
                throw new JspException(e);
            } catch (IOException e) {
                throw new JspException(e);
            } catch (SystemException e) {
                throw new JspException(e);
            } catch (NotAuthorizedException e) {
                throw new JspException(e);
            }

        }

        url = null;

        return SKIP_BODY;
    }

    public int doEndTag() throws JspException {
         return EVAL_PAGE;
    }


}
