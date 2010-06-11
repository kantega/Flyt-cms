<%@ page import="java.util.List" %>
<%@ page import="no.kantega.publishing.common.data.Site" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
    Site site = (Site)request.getAttribute("site");
    List hostnames = site.getHostnames();
%>
<kantega:section id="title">
    <kantega:label key="aksess.sites.title"/>
</kantega:section>

<kantega:section id="content">
    <form name="myform" action="EditDomainNames.action" method="post">
        <input type="hidden" name="siteId" value="${site.id}">
        <admin:box>
            <h1><kantega:label key="aksess.sites.title"/></h1>

            <div class="formElement">
                <div class="heading">
                    <label><kantega:label key="aksess.site.name"/></label>
                </div>
                <div class="content">${site.name}</div>
            </div>

            <div class="formElement">
                <div class="heading">
                    <label><kantega:label key="aksess.site.alias"/></label>
                </div>
                <div class="content">${site.alias}</div>
            </div>

            <div class="formElement">
                <div class="heading">
                    <label><kantega:label key="aksess.site.domains"/></label>
                </div>
                <div class="content">
                    <table>
                        <%

                            for (int i = 0; i < Math.min(hostnames.size() + 10, 40); i++) {
                                String hostname = "";
                                if (i < hostnames.size()) {
                                    hostname = (String)hostnames.get(i);
                                }
                        %>
                        <tr>
                            <td width="80"><kantega:label key="aksess.site.domain"/> <%=(i+1)%></td>
                            <td><input type="text" name="hostname<%=i%>" value="<%=hostname%>" size="40" maxlength="128"></td>
                        </tr>
                        <%
                            }
                        %>
                    </table>
                    <br>
                    <div class="ui-state-highlight"><kantega:label key="aksess.site.domains.tip"/></div>
                </div>
            </div>
            <div class="buttonGroup">
                <span class="button"><input type="submit" class="ok" value="<kantega:label key="aksess.button.ok"/>"></span>
                <span class="button"><input type="button" onclick="location.href='ListSites.action'" class="button cancel" value="<kantega:label key="aksess.button.cancel"/>"></span>
            </div>            
        </admin:box>
    </form>


</kantega:section>
<%@ include file="../layout/administrationLayout.jsp" %>