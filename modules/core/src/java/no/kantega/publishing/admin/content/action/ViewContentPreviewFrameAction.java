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

package no.kantega.publishing.admin.content.action;

import no.kantega.publishing.admin.AdminSessionAttributes;
import no.kantega.publishing.client.ContentRequestDispatcher;
import no.kantega.publishing.common.Aksess;
import no.kantega.publishing.common.data.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ViewContentPreviewFrameAction extends AbstractController {
    private ContentRequestDispatcher contentRequestDispatcher;


    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(true);
        Content content = (Content)session.getAttribute(AdminSessionAttributes.CURRENT_EDIT_CONTENT);
        if (content != null) {
            contentRequestDispatcher.dispatchContentRequest(content, getServletContext(), request, response);
        } else {
            request.getRequestDispatcher(Aksess.getStartPage()).forward(request, response);
        }
        return null;
    }

    @Autowired
    public void setContentRequestDispatcher(ContentRequestDispatcher contentRequestDispatcher) {
        this.contentRequestDispatcher = contentRequestDispatcher;
    }
}
