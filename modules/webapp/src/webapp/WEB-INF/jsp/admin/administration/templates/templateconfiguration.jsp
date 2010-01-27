<%@ page import="no.kantega.publishing.common.data.TemplateConfiguration" %>
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
<kantega:section id="title">
    <kantega:label key="aksess.templateconfig.title"/>
</kantega:section>

<kantega:section id="content">
    <%
        TemplateConfiguration tc = (TemplateConfiguration)request.getAttribute("templateConfiguration");
    %>
    <div class="fieldset">
        <fieldset>
            <h1><kantega:label key="aksess.templateconfig.title"/></h1>
            <table>
                <tr>
                    <th>&nbsp;</th>
                    <th><kantega:label key="aksess.templateconfig.items"/></th>
                </tr>
                <tr class="tableRow0">
                    <td><a href="ListAssociationCategories.action"><kantega:label key="aksess.associationcategories.title"/></a></td>
                    <td><%=tc.getAssociationCategories().size()%></td>
                </tr>
                <tr class="tableRow1">
                    <td><a href="ListContentTemplates.action"><kantega:label key="aksess.contenttemplates.title"/></a></td>
                    <td><%=(tc.getContentTemplates().size() + tc.getMetadataTemplates().size()) %></td>
                </tr>
                <tr class="tableRow0">
                    <td><a href="ListDisplayTemplates.action"><kantega:label key="aksess.displaytemplates.title"/></a></td>
                    <td><%=tc.getDisplayTemplates().size()%></td>
                </tr>
            </table>

            <div class="ui-state-highlight"><kantega:label key="aksess.templateconfig.info"/></div>

            <form action="ReloadTemplateConfiguration.action" method="post">
                <div class="buttonGroup">
                    <span class="button"><input type="submit" name="submit" value="<kantega:label key="aksess.templateconfig.reload"/>"></span>
                </div>
            </form>

        </fieldset>
    </div>

    <c:if test="${not empty errors}">

    <div class="fieldset">
        <fieldset>
            <h1><kantega:label key="aksess.templateconfig.error"/></h1>
                <table>
                    <tr>
                        <th><kantega:label key="aksess.templateconfig.error.object"/></th>
                        <th><kantega:label key="aksess.templateconfig.error.message"/></th>
                    </tr>
                    <c:forEach var="errorMessage" items="${errors}" varStatus="status">
                        <tr class="tableRow${status.index mod 2}">
                            <td>${errorMessage.object}</td>
                            <td><kantega:label key="${errorMessage.message}"/>: ${errorMessage.data}</td>
                        </tr>
                    </c:forEach>
                </table>
        </fieldset>
    </div>
    </c:if>
</kantega:section>
<%@ include file="../../layout/administrationLayout.jsp" %>