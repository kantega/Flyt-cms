<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ page import="no.kantega.commons.client.util.ValidationErrors"%>
<%@ page import="no.kantega.commons.util.LocaleLabels"%>
<%@ page import="no.kantega.publishing.admin.content.util.AttributeHelper"%>
<%@ page import="no.kantega.publishing.common.Aksess"%>

<%--
  ~ Copyright 2009 Kantega AS
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~    http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<%
    {
        ValidationErrors errors = (ValidationErrors)request.getAttribute("errors");
        String display = "none";
        if ((errors != null) && (errors.getLength() > 0)) {
            display = "block";
        }
%>
<div id="errorMessageArea" style="display:<%=display%>;">
    <div class="ui-state-error">
            <strong><kantega:label key="aksess.editcontent.error"/></strong>
            <ul>
                <%
                    if (errors != null) {
                        int noErrors = errors.getLength();
                        for (int i = 0; i < noErrors; i++) {
                            String field = errors.getField(i);
                            if (field != null) {
                                field = AttributeHelper.getInputFieldName(field);
                                out.write("<LI>" + errors.getMessage(i, Aksess.getDefaultAdminLocale()) + "&nbsp;[<A href=\"#\" onclick=\"document.myform['" + field + "'].focus()\">" + LocaleLabels.getLabel("aksess.feil.highlightfield", Aksess.getDefaultAdminLocale()) + "</A>]</LI>");
                            } else {
                                out.write("<LI>" + errors.getMessage(i, Aksess.getDefaultAdminLocale()) + "</LI>");
                            }
                        }
                    }
                %>
            </ul>
    </div>
</div>
<%
        String infomessage = request.getParameter("infomessage");
        if (infomessage != null && infomessage.length() > 0) {
            String key = "aksess.infomessage." + infomessage;
%>
            <div class="ui-state-highlight">
                <kantega:label key="<%=key%>"/>
            </div>
<%
        }
    }
%>
