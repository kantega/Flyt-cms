<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess" %>
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
<kantega:section id="title">
    <kantega:label key="aksess.displaytemplates.title"/>
</kantega:section>

<kantega:section id="content">
    <div class="fieldset">
        <fieldset>
            <legend><kantega:label key="aksess.displaytemplates.title"/></legend>
            <c:if test="${!empty templates}">
                <table>
                    <tr>
                        <th><kantega:label key="aksess.templates.id"/></th>
                        <th><kantega:label key="aksess.templates.template"/></th>
                        <th><kantega:label key="aksess.templates.view"/></th>
                        <th><kantega:label key="aksess.templates.publicid"/></th>
                        <th>&nbsp;</th>
                    </tr>
                    <c:forEach var="template" items="${templates}" varStatus="status">
                        <tr class="tableRow${status.index mod 2}">
                            <td>${template.id}</td>
                            <td>${template.name}</td>
                            <td>${template.view}</td>
                            <td>${template.publicId}</td>
                            <td class="buttonGroup">
                                <a href="ListDisplayTemplateUsages.action?templateId=${template.id}" class="button show"><span><kantega:label key="aksess.button.vis"/></span></a>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </c:if>
            <c:if test="${empty templates}">
                <kantega:label key="aksess.templates.notemplates"/>
            </c:if>
        </fieldset>
    </div>

</kantega:section>
<%@ include file="../../layout/administrationLayout.jsp" %>
