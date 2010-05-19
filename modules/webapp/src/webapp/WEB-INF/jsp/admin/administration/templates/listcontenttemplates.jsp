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
    <kantega:label key="aksess.contenttemplates.title"/>
</kantega:section>

<kantega:section id="content">
    <div class="fieldset">
        <fieldset>
            <h1><kantega:label key="aksess.contenttemplates.title"/></h1>
            <c:if test="${!empty templates}">
                <table border="0" cellspacing="0" cellpadding="0" width="600">
                    <tr class="tableHeading">
                        <td><kantega:label key="aksess.templates.id"/></td>
                        <td><kantega:label key="aksess.templates.template"/></td>
                        <td><kantega:label key="aksess.templates.templatefile"/></td>
                        <td><kantega:label key="aksess.templates.publicid"/></td>
                    </tr>
                    <c:forEach var="template" items="${templates}" varStatus="status">
                        <tr class="tableRow${status.index mod 2}">
                            <td>${template.id}</td>
                            <td>${template.name}</td>
                            <td>${template.templateFile}</td>
                            <td>${template.publicId}</td>
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
