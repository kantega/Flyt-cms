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

import no.kantega.publishing.admin.AdminRequestParameters;
import no.kantega.publishing.common.Aksess;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

public class HeaderDependenciesTag extends SimpleTagSupport {

    @Override
    public void doTag() throws JspException, IOException {
        PageContext pageContext = (PageContext) getJspContext();
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        JspWriter out = pageContext.getOut();

        String scrollTo = (String)request.getAttribute("scrollTo");

        out.write("<link rel=\"stylesheet\" type=\"text/css\" href=\""+request.getContextPath()+"/wro-oa/miniaksess.css\">\n");
        out.write("<script type=\"text/javascript\">\n" +
                "        var properties = {\n" +
                "            contextPath : '"+((HttpServletRequest) pageContext.getRequest()).getContextPath()+"',\n" +
                "            debug : "+Aksess.isJavascriptDebugEnabled()+",\n" +
                "            contentRequestHandler : '"+ Aksess.CONTENT_REQUEST_HANDLER+"',\n" +
                "            thisId : '"+ AdminRequestParameters.THIS_ID+"'\n" +
                "        };\n");
        if (scrollTo != null) {
            out.write("  function scrollTo() {\n" +
                    "            var elementPosition = $(\"#" + scrollTo + " .buttonGroup:last\").offset().top;\n" +
                    "            $(\"body\").scrollTop(elementPosition);\n" +
                    "        }\n" +
                    "\n");
            out.write("  $(document).ready(function(){\n" +
                    "       setTimeout(scrollTo, 500);\n" +
                    "    });");
        } else {
            out.write("  $(document).ready(function(){\n" +
                    "       // Set focus to first input field in form\n" +
                    "       $(\"#EditContentForm\").find(\"input[type='text']:first\").focus();\n" +
                    "    });");
        }
        out.write("" +
                "        $.datepicker.setDefaults($.datepicker.regional['']);\n" +
                "        $.datepicker.setDefaults($.datepicker.regional['" + Aksess.getDefaultAdminLocale().getCountry() + "']);\n" +
                "        $.datepicker.setDefaults( {firstDay: 1, dateFormat:'dd.mm.yy'});" +
                "    </script>");
        out.write("<script type=\"text/javascript\" src=\""+request.getContextPath()+"/aksess/js/aksess-i18n.jjs\"></script>\n");
        out.write("<script type=\"text/javascript\" src=\""+request.getContextPath()+"/wro-oa/miniaksess.js\"></script>\n");
        out.write("<script type=\"text/javascript\" src=\""+ request.getContextPath()+"/aksess/tiny_mce/tiny_mce_gzip.js\"></script>\n");
    }
}
