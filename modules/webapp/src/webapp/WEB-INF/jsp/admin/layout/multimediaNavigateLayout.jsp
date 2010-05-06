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
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/mimetypes.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/jquery.lazyload.mini.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/navigate.jjs"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/ajaxupload.3.5.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/multimedia.jjs"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/dwr/interface/MultimediaClipboardHandler.js"></script>

</kantega:section>

<kantega:section id="topMenu">
    <%@include file="fragments/topMenu.jsp"%>
</kantega:section>

<kantega:section id="modesMenu">
<div class="buttonGroup search">
    <form action="" method="get">
        <input type="text" id="SearchQuery" class="query multimedia" name="query multimedia">
        <input type="submit" id="SearchButton" value="" title="<kantega:label key="aksess.search.submit"/>">
    </form>
</div>
</kantega:section>

<kantega:section id="toolsMenu">
    <a href="#" class="button disabled" id="UploadButton"><span class="newfile"><kantega:label key="aksess.tools.upload"/></span></a>
    <a href="#" class="button disabled" id="NewFolderButton"><span class="newfolder"><kantega:label key="aksess.tools.newfolder"/></span></a>
    <a href="#" class="button disabled" id="DeleteFolderButton"><span class="delete"><kantega:label key="aksess.tools.deletefolder"/></span></a>
</kantega:section>

<kantega:section id="body">
    <div id="Content" class="navigateMultimedia">
        <div id="Navigation">

            <div id="Navigator"></div>
            <div id="Framesplit"></div>
        </div>

        <div id="MainPane">
            <div id="Statusbar">
                <div id="Breadcrumbs"></div> <%-- Loaded by MultimediaPropertiesAction --%>
            </div>
            <div class="infoslider"></div>            
            <kantega:getsection id="content"/>
        </div>
    </div>

    <%-- Including the context menus so they are available to jQyery. They are default hidden (by css) from view. --%>
    <%@include file="fragments/contextMenu-media.jsp"%>
    <%@include file="fragments/contextMenu-folder.jsp"%>    

</kantega:section>


<%@include file="commonLayout.jsp"%>