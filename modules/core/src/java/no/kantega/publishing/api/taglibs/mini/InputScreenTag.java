/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.api.taglibs.mini;

import no.kantega.commons.exception.InvalidFileException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.admin.content.InputScreenRenderer;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.enums.AttributeDataType;
import no.kantega.publishing.common.exception.InvalidTemplateException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

/**
 * User: Kristian Selnæs
 * Date: 23.mar.2010
 * Time: 12:22:03
 */
public class InputScreenTag extends SimpleTagSupport {

    @Override
    public void doTag() throws JspException, IOException {
        PageContext pageContext = (PageContext) getJspContext();
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();

        Content currentEditContent = (Content) request.getAttribute(AdminSessionAttributes.CURRENT_EDIT_CONTENT);

        try {
            pageContext.include("/WEB-INF/jsp/admin/layout/fragments/infobox.jsp");

            InputScreenRenderer screenRenderer = new InputScreenRenderer(pageContext, currentEditContent, AttributeDataType.CONTENT_DATA);
            screenRenderer.generateInputScreen();
            if (screenRenderer.hasHiddenAttributes()) {
                pageContext.include("/WEB-INF/jsp/admin/layout/fragments/addattributebutton.jsp");
            }
        } catch (InvalidFileException e) {
            Log.error(this.getClass().getName(), e, null, null);
        } catch (InvalidTemplateException e) {
            Log.error(this.getClass().getName(), e, null, null);
        } catch (ServletException e) {
            Log.error(this.getClass().getName(), e, null, null);
        }
    }
}
