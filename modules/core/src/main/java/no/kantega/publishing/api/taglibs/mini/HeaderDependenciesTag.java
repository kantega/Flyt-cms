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

import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.commons.taglib.expires.ResourceKeyProvider;
import no.kantega.publishing.admin.AdminRequestParameters;
import no.kantega.publishing.common.Aksess;
import org.springframework.beans.BeansException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

public class HeaderDependenciesTag extends SimpleTagSupport {

    private boolean includejquery = false;

    private ResourceKeyProvider provider;

    @Override
    public void doTag() throws JspException, IOException {
        PageContext pageContext = (PageContext) getJspContext();
        initIfNecessary(pageContext);

        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        JspWriter out = pageContext.getOut();

        String scrollTo = (String)request.getAttribute("scrollTo");
        ValidationErrors errors = (ValidationErrors)request.getAttribute("errors");

        out.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + getExpireUrl(request, "/wro-oa/miniaksess.css") + "\">\n");
        if (includejquery) {
            out.write("<script type=\"text/javascript\" src=\""+ getExpireUrl(request, "/wro-oa/jquery-all.js") + "\"></script>\n");
        }
        out.write("<script type=\"text/javascript\" src=\""+ getExpireUrl(request, "/wro-oa/miniaksess.js") + "\"></script>\n");
        out.write("<script type=\"text/javascript\" src=\""+ getExpireUrl(request, "/aksess/js/aksess-i18n.jjs") + "\"></script>\n");
        out.write("<script type=\"text/javascript\" src=\""+ getExpireUrl(request, "/flytcms/tinymce/tinymce.min.js") + "\"></script>\n");

        out.write("<script type=\"text/javascript\">\n" +
                "        if (typeof properties == 'undefined') {\n" +
                "            var properties = { };\n" +
                "        } " +
                "        properties.contextPath = '"+((HttpServletRequest) pageContext.getRequest()).getContextPath()+"',\n" +
                "        properties.debug = "+Aksess.isJavascriptDebugEnabled()+",\n" +
                "        properties.contentRequestHandler = '"+ Aksess.CONTENT_REQUEST_HANDLER+"',\n" +
                "        properties.thisId = '"+ AdminRequestParameters.THIS_ID+"'\n");
        if (scrollTo != null) {
            out.write("  function scrollTo() {\n" +
                    "            var elementPosition = $(\"#" + scrollTo + " .contentAttributeRepeaterRow:last\").offset().top;\n" +
                    "            if (elementPosition > 100) {" +
                    "                elementPosition -= 100;" +
                    "            } else { " +
                    "                elementPosition = 0;" +
                    "            }" +
                    "            $(\"html,body\").scrollTop(elementPosition);\n" +
                    "        }\n" +
                    "\n");
            out.write("  $(document).ready(function(){\n" +
                    "       setTimeout(scrollTo, 500);\n" +
                    "    });\n");
        } else if (errors == null || errors.getLength() == 0) {
            out.write("  $(document).ready(function(){\n" +
                    "       // Set focus to first input field in form\n" +
                    "       $(\"#EditContentForm\").find(\"input[type='text']:first\").focus();\n" +
                    "    });\n");
        }

        out.write(
                "        function setupDatepicker() {\n" +
                        "            $.datepicker.setDefaults($.datepicker.regional['']);\n" +
                        "            $.datepicker.setDefaults($.datepicker.regional['NO']);\n" +
                        "            $.datepicker.setDefaults( {firstDay: 1, dateFormat:'dd.mm.yy'});\n" +
                        "        }\n" +
                        "        if ($.datepicker) {\n" +
                        "            setupDatepicker();\n" +
                        "        } else {\n" +
                        "            $(document).ready(setupDatepicker);\n" +
                        "        }" +
                "    </script>");

    }

    private String getExpireUrl(HttpServletRequest request, String url) {
        return request.getContextPath() + "/expires/" + provider.getUniqueKey(request, url) + url ;
    }

    private void initIfNecessary(PageContext pageContext) throws JspException {
        if (provider == null) {
            WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext());
            try {
                provider = context.getBean(ResourceKeyProvider.class);
            } catch (BeansException e) {
                throw new JspException("Could not find ResourceKeyProvider", e);
            }
        }
    }

    public void setIncludejquery(boolean includejquery) {
        this.includejquery = includejquery;
    }
}
