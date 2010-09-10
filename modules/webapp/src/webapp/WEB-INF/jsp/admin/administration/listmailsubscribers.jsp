<%@ page import="java.util.List" %>
<%@ page import="no.kantega.publishing.common.data.UserContentChanges" %>
<%@ page import="java.net.URLEncoder" %>
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
    <kantega:label key="aksess.mailsubscription.title"/>
</kantega:section>

<kantega:section id="content">
    <admin:box>
        <h1><kantega:label key="aksess.mailsubscription.title"/></h1>
        <table class="fullWidth">
            <tr class="tableHeading">
                <td><strong><kantega:label key="aksess.mailsubscription.email"/></strong></td>
                <td>&nbsp;</td>
            </tr>
            <%
                String queryStringEncoding = Aksess.getQueryStringEncoding();
            %>
            <c:forEach var="email" items="${subscriptions}" varStatus="status">
                <%
                    String email = (String)pageContext.getAttribute("email");
                    String emailEnc = URLEncoder.encode(email, queryStringEncoding);
                %>
                <tr class="tableRow${status.index mod 2}">
                    <td><a href="mailto:<c:out value="${email}"/>"><c:out value="${email}"/></a></td>
                    <td>
                        <a href="ViewMailSubscribers.action?delete=<%=emailEnc%>" class="button delete"><kantega:label key="aksess.button.delete"/></a>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </admin:box>
    </form>

</kantega:section>
<%@ include file="../layout/administrationLayout.jsp" %>