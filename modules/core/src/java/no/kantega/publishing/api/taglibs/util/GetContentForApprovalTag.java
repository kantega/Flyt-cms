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

import no.kantega.publishing.common.service.ContentManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.List;

public class GetContentForApprovalTag extends TagSupport {
    private static final Logger log = LoggerFactory.getLogger(GetContentForApprovalTag.class);
    private static final String SOURCE = "aksess.GetContentForApprovalTag";

    private String name = null;

    public void setName(String name) {
        this.name = name.toLowerCase();
    }

    public int doStartTag() throws JspException {
        try {
            HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
            ContentManagementService cms = new ContentManagementService(request);
            List content = cms.getContentListForApproval();
            request.setAttribute(name, content);
        } catch (Exception e) {
            log.error("", e);
            throw new JspTagException(SOURCE, e);
        }

        return SKIP_BODY;
    }


    public int doEndTag() throws JspException {
        name = null;

        return EVAL_PAGE;
    }
}

