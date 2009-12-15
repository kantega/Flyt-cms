<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  ~ Copyright 2009 Kantega AS
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<kantega:section id="head">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/admin/css/navigate.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/admin/css/multimedia.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/navigate.jjs"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/ajaxupload.3.5.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/multimedia.jjs"></script>

</kantega:section>

<kantega:section id="topMenu">
    <%@include file="fragments/topMenu.jsp"%>
</kantega:section>

<kantega:section id="modesMenu">

</kantega:section>

<kantega:section id="toolsMenu">
    <!-- TODO: Menyen her må lastes via ajax eller noe for at riktige knapper skal vises  -->
    <a href="#" class="button" id="UploadButton"><span class="upload"><kantega:label key="aksess.tools.upload"/></span></a>
    <a href="#" class="button" id="NewFolderButton"><span class="newfolder"><kantega:label key="aksess.tools.newfolder"/></span></a>
</kantega:section>

<kantega:section id="body">
    <div id="Content" class="navigateMultimedia">
        <div id="Navigation">

            <div id="Navigator"></div>
            <div id="Framesplit"></div>
        </div>

        <div id="MainPane">
            <kantega:getsection id="content"/>
        </div>
    </div>

</kantega:section>


<%@include file="commonLayout.jsp"%>