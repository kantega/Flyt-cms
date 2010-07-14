<%@ page import="no.kantega.publishing.admin.content.InputScreenRenderer" %>
<%@ page import="no.kantega.publishing.common.data.enums.AttributeDataType" %>
<%@ page import="no.kantega.publishing.common.data.Content" %>
<%@ page import="no.kantega.publishing.common.data.ContentIdentifier" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="no.kantega.publishing.admin.AdminSessionAttributes" %>
<%@ page import="no.kantega.publishing.security.data.enums.Privilege" %>
<%@ page import="no.kantega.publishing.security.SecuritySession" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
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
<c:set var="editActive" value="true"/>
<c:set var="versionsActive" value="true"/>
<kantega:section id="title">
    <kantega:label key="aksess.versions.title"/>
</kantega:section>

<kantega:section id="content">
    <script language="Javascript" type="text/javascript">
        function selectVersion(version) {
            document.activeversion.version.value = version;
            document.activeversion.submit();
        }

        function deleteVersion(version) {
            document.deleteversion.version.value = version;
            document.deleteversion.submit();
        }
    </script>
        <%@ include file="fragments/infobox.jsp" %>
        <table border="0" cellspacing="0" cellpadding="0" width="100%">
            <thead>
            <tr>
                <th colspan="2"><kantega:label key="aksess.versions.version"/></th>
                <th><kantega:label key="aksess.versions.lastmodified"/></th>
                <th><kantega:label key="aksess.versions.modifiedby"/></th>
                <th><kantega:label key="aksess.versions.minorchange"/></th>
                <th><kantega:label key="aksess.versions.status"/></th>
                <th>&nbsp;</th>
            </tr>
            </thead>
            <tbody>
            <%
                SecuritySession securitySession = SecuritySession.getInstance(request);
                List allVersions = (List)request.getAttribute("allVersions");
                Content current = (Content)session.getAttribute(AdminSessionAttributes.CURRENT_EDIT_CONTENT);
                for (int i = 0; i < allVersions.size(); i++) {
                    Content c = (Content)allVersions.get(i);
                    Date d = c.getLastModified();
                    DateFormat df = new SimpleDateFormat(Aksess.getDefaultDateFormat());
                    String modifiedDate = "";
                    try {
                        modifiedDate = df.format(d);
                    } catch (NumberFormatException e) {
                    }
                    String title = c.getTitle();
                    if (title.length() > 30) {
                        title = title.substring(0, 27) + "...";
                    }
                    String statusKey = "aksess.versions.status." + c.getStatus();
                    if (c.getStatus() == ContentStatus.PUBLISHED) {
                        statusKey += "_" + c.getVisibilityStatus();
                    }
            %>
            <tr class="tableRow<%=(i%2)%>">
                <td><%=c.getVersion()%></td>
                <td><%=title%></td>
                <td><%=modifiedDate%></td>
                <td><%=c.getModifiedBy()%></td>
                <td><input type="checkbox" name="cb<%=i%>" disabled="true" <%if (c.isMinorChange()) out.write("checked");%>></td>
                <td><kantega:label key="<%=statusKey%>"/></td>
                <td>
                    <a href="<%=current.getUrl()%>&version=<%=c.getVersion()%>" target="_new" class="button show"><span><kantega:label key="aksess.button.show"/></span></a>
                    <a href="Javascript:selectVersion(<%=c.getVersion()%>)" class="button edit"><span><kantega:label key="aksess.button.edit"/></span></a>
                    <% if (c.getStatus() != ContentStatus.PUBLISHED && securitySession.isAuthorized(current, Privilege.APPROVE_CONTENT)) {%>
                    <a href="Javascript:deleteVersion(<%=c.getVersion()%>)" class="button delete"><span><kantega:label key="aksess.button.delete"/></span></a>
                    <%}%>
                </td>
            </tr>
            <%
                }
            %>
            </tbody>
        </table>

        <div class="ui-state-highlight">
            <kantega:label key="aksess.versions.hjelp"/>
            <c:if test="${showMaxVersions}">
                <br><kantega:label key="aksess.versions.hjelp2"/> ${maxVersions} <kantega:label key="aksess.versions.hjelp3"/>
            </c:if>
        </div>
</kantega:section>
<%@ include file="../layout/editContentLayout.jsp" %>