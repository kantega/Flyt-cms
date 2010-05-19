<%@ page import="java.util.List" %>
<%@ page import="no.kantega.publishing.common.data.UserContentChanges" %>
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
    <kantega:label key="aksess.userchanges.title"/>
</kantega:section>

<kantega:section id="content">
    <form name="myform" action="" method="get">

    <div class="fieldset">
        <fieldset>
            <h1><kantega:label key="aksess.userchanges.title"/></h1>

            <div class="formElement">
            <div class="inputs">
                <kantega:label key="aksess.userchanges.month"/>
                <select name="months" onchange="document.myform.submit()">
                    <option value="1" <c:if test="${month == 1}">selected</c:if>>1</option>
                    <option value="3" <c:if test="${month == 3}">selected</c:if>>3</option>
                    <option value="6" <c:if test="${month == 6}">selected</c:if>>6</option>
                    <option value="12" <c:if test="${month == 12}">selected</c:if>>12</option>
                </select>
            </div>

            </div>
            <table class="fullWidth">
            <%
                List userchanges = (List)request.getAttribute("userChanges");
                if (userchanges != null) {
                    int total = 0;
            %>
            <tr>
                <th><kantega:label key="aksess.userchanges.username"/></th>
                <th><kantega:label key="aksess.userchanges.changes"/></th>
            </tr>
            <%
                int i = 0;
                for (i = 0; i < userchanges.size(); i++) {
                    UserContentChanges ucc = (UserContentChanges)userchanges.get(i);
                    total += ucc.getNoChanges();
            %>
            <tr class="tableRow<%=(i%2)%>">
                <td><a href="ListUserChanges.action?username=<%=ucc.getUserName()%>"><%=ucc.getUserName()%></a></td>
                <td align="right"><%=ucc.getNoChanges()%></td>
            </tr>
            <%

                }
            %>
            <tr class="tableRow<%=(i%2)%>">
                <td><strong><kantega:label key="aksess.userchanges.total"/></strong></td>
                <td align="right"><strong><%=total%></strong></td>
            </tr>
            <%
                }

            %>
            </table>

            <div class="ui-state-highlight"><kantega:label key="aksess.userchanges.help"/></div>
        </fieldset>
    </div>
    </form>

</kantega:section>
<%@ include file="../layout/administrationLayout.jsp" %>