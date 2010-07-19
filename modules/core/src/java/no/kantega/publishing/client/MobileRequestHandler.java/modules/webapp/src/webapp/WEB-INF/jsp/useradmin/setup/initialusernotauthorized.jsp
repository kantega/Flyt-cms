<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1"%>
<%@ page import="no.kantega.publishing.common.Aksess" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/aksess" prefix="aksess" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/commons" prefix="kantega" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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


<kantega:section id="title">Can't create role</kantega:section>

<kantega:section id="head">
    <script type="text/javascript" src="${pageContext.request.contextPath}/login/js/formlabels.js"></script>
</kantega:section>

<kantega:section id="body">
    <h1>Not authorized to create default user and role</h1>
    <p>
        The system could not find any roles and you are not authorized to create any from this IP-address.
    </p>

    <p>
        Access this page using http://localhost/ from the webserver.
    </p>
</kantega:section>

<%@ include file="../../admin/layout/loginLayout.jsp" %>

