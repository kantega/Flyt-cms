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

import no.kantega.commons.util.StringHelper;
import no.kantega.publishing.admin.content.util.HTMLEditorHelper;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * User: Anders Skar, Kantega AS
 * Date: Sep 5, 2008
 * Time: 11:16:02 AM
 */
public class CleanupHTMLAction  implements Controller {
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String html = request.getParameter("html");

        html = StringHelper.replace(html, "\u2022", "<li>");

        HTMLEditorHelper helper = new  HTMLEditorHelper(request);

        response.setContentType("text/plain; charset=UTF-8");
        html = helper.cleanupHTML(html);

        Writer writer = new OutputStreamWriter(response.getOutputStream(), "UTF-8");
        writer.write(html);
        writer.close();

        return null;        
    }
}
