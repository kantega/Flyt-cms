<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="aksess" uri="http://www.kantega.no/aksess/tags/aksess"%>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ page import="no.kantega.publishing.common.Aksess" %>
<%--
~ Copyright 2009 Kantega AS
~
~ Licensed under the Apache License, Version 2.0 (the "License");
~ you may not use this file except in compliance with the License.
~ You may obtain a copy of the License at
~
~  http://www.apache.org/licenses/LICENSE-2.0
~
~ Unless required by applicable law or agreed to in writing, software
~ distributed under the License is distributed on an "AS IS" BASIS,
~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
~ See the License for the specific language governing permissions and
~ limitations under the License.
--%>

<c:set var="organizeActive" value="true"/>
<kantega:section id="title">
    <kantega:label key="aksess.navigate.title"/>
</kantega:section>

<kantega:section id="contentclass">organizeSubpages</kantega:section>

<kantega:section id="head extras">
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/organizesubpages.jjs"></script>
    <script type="text/javascript">
        $(document).ready(function(){
            openaksess.common.debug("organizesubpages.$(document).ready()");
            openaksess.organizesubpages.currentUrl = "${currentNavigateContent.url}";
            openaksess.organizesubpages.updateSubPageList();

        });        
    </script>
</kantega:section>

<kantega:section id="content">
    <div id="MainPaneContent">
        <%-- The content is loaded with ajax by the ListSubPagesAction --%>
        <div id="SubPages"></div>
    </div>
</kantega:section>

<%@include file="../layout/contentNavigateLayout.jsp"%>
