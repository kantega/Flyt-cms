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
    <kantega:label key="aksess.sites.title"/>
</kantega:section>

<kantega:section id="content">

    <div class="fieldset">
        <fieldset>
            <h1><kantega:label key="aksess.sites.title"/></h1>

            <table>
                <tr>
                    <th><strong><kantega:label key="aksess.site.name"/></strong></th>
                    <th><strong><kantega:label key="aksess.site.alias"/></strong></th>
                    <th>&nbsp;</th>
                </tr>
                <c:forEach var="site" items="${sites}" varStatus="status">
                    <tr class="tableRow${status.index mod 2}">
                        <td>${site.name}</td>
                        <td>${site.alias}</td>
                        <td>
                            <a href="EditDomainNames.action?siteId=${site.id}" class="button edit"><span><kantega:label key="aksess.site.editdomains"/></span></a>
                            <a href="CreateRoot.action?siteId=${site.id}" class="button create"><span><kantega:label key="aksess.site.createhomepage"/></span></a>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </fieldset>

    </div>

</kantega:section>
<%@ include file="../layout/administrationLayout.jsp" %>