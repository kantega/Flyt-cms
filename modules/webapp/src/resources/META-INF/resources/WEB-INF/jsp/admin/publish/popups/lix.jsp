<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="admin" uri="http://www.kantega.no/aksess/tags/admin" %>
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
<kantega:section id="title"><kantega:label key="lix.title"/></kantega:section>

<kantega:section id="head">
</kantega:section>

<kantega:section id="body">

    <%
        String lixScore  = request.getParameter("lix");
        String words     = request.getParameter("wc");
        String lwords    = request.getParameter("lwc");
        String sentances = request.getParameter("sent");

        request.setAttribute("lix", Integer.parseInt(lixScore));
        request.setAttribute("wc", request.getParameter("wc"));
        request.setAttribute("lwc", request.getParameter("lwc"));
        request.setAttribute("sent", request.getParameter("sent"));
    %>

    <h2><kantega:label key="lix.title"/>: ${lix}</h2>

    <c:if test="${lix < 25}"><kantega:label key="lix.desc.veryeasy"/></c:if>

    <c:if test="${lix >= 25 && lix < 35}"><kantega:label key="lix.desc.easy"/></c:if>

    <c:if test="${lix >= 35 && lix < 45}"><kantega:label key="lix.desc.medium"/></c:if>

    <c:if test="${lix >= 45 && lix < 55}"><kantega:label key="lix.desc.hard"/></c:if>

    <c:if test="${lix > 55}"><kantega:label key="lix.desc.veryhard"/></c:if>

    <br><br>

    ${wc} <kantega:label key="lix.words"/>, ${lwc} <kantega:label key="lix.longwords"/>, ${sent} <kantega:label key="lix.sentences"/>

</kantega:section>
<%@ include file="../../layout/popupLayout.jsp" %>