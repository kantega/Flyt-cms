<%@ page import="no.kantega.publishing.common.data.enums.ContentStatus" %>
<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/admin" prefix="admin" %>
<%@ page buffer="none" %>
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
<kantega:section id="head">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/admin/css/multimedia.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/admin/css/jquery.Jcrop.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/mimetypes.css">
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/editmultimedia.jjs"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/jquery.Jcrop.min.js"></script>
    <script type="text/javascript">
        $(document).ready(function(){
            bindToolButtons();
        });

        /**
         * Registers click event actions to each tool
         */
        function bindToolButtons() {
            <c:if test="${canEdit && isImage}">
                $("#ToolsMenu .button .crop").click(function(){
                    location.href = "ImageCrop.action?id=${media.id}";
                });
            </c:if>
            <c:if test="${canEdit && isImage}">
                $("#ToolsMenu .button .imagemap").click(function(){
                    location.href = "ImageMap.action?id=${media.id}";
                });
            </c:if>
            <c:if test="${canEdit}">
                $("#ToolsMenu .button .delete").click(function(){
                    openaksess.common.modalWindow.open({title:'<kantega:label key="aksess.confirmdelete.title"/>', iframe:true, href: "${pageContext.request.contextPath}/admin/multimedia/DeleteMultimedia.action?id=${media.id}",width: 450, height:250});
                });
            </c:if>
        }
    </script>

</kantega:section>

<kantega:section id="topMenu">
    <%@include file="fragments/topMenu.jsp"%>
</kantega:section>

<kantega:section id="modesMenu">
    <div class="buttonGroup">
        <span class="singlebutton"><a href="Navigate.action" class="button first"><span class="back"><kantega:label key="aksess.mode.back"/></span></a></span>
    </div>
</kantega:section>

<kantega:section id="toolsMenu">
    <div class="buttonGroup">
        <a href="#" class="button <c:if test="${!(canEdit && isImage)}">disabled</c:if>"><span class="crop"><kantega:label key="aksess.tools.crop"/></span></a>
        <a href="#" class="button <c:if test="${!(canEdit && isImage)}">disabled</c:if>"><span class="imagemap"><kantega:label key="aksess.tools.imagemap"/></span></a>
        <a href="#" class="button <c:if test="${!(canEdit)}">disabled</c:if>"><span class="delete"><kantega:label key="aksess.tools.delete"/></span></a>
    </div>
</kantega:section>

<kantega:section id="body">

    <div id="Content" class="multimedia">
        <div id="MainPane">
            <div id="MultimediaMain">
                <div id="MultimediaPane">
                    <kantega:getsection id="content"/>
                </div>
            </div>
            <div id="EditMultimediaButtons" class="buttonBar">
                <kantega:getsection id="editbuttons"/>
            </div>
        </div>

        <div id="SideBar">
            <c:if test="${isPropertyPaneEditable}">
            <form name="editmediaform" id="EditMultimediaForm" action="EditMultimedia.action" method="post" enctype="multipart/form-data">
                <input type="hidden" name="id" value="${media.id}">
                <input type="hidden" name="changed" value="false">
                <input type="hidden" name="insert" value="false">
                <input type="hidden" id="MaxWidth" name="maxWidth" value="-1">
                </c:if>
                <div class="sidebarFieldset">
                    <fieldset>
                        <legend><kantega:label key="aksess.multimedia.medianame"/></legend>
                        <input type="text" class="fullWidth" name="name" id="MultimediaName" value="<c:out value="${media.name}"/>" maxlength="255" <c:if test="${!isPropertyPaneEditable}">disabled="disabled"</c:if>>
                    </fieldset>
                </div>
                <div class="sidebarFieldset">
                    <fieldset>
                        <legend><kantega:label key="aksess.multimedia.altname"/></legend>
                        <input type="text" class="fullWidth" name="altname" id="MultimediaAltName" value="<c:out value="${media.altname}"/>" maxlength="255" <c:if test="${!isPropertyPaneEditable}">disabled="disabled"</c:if>>
                        <div class="ui-state-highlight">
                            <kantega:label key="aksess.multimedia.altinfo"/>
                        </div>
                    </fieldset>
                </div>
                <div class="sidebarFieldset">
                    <fieldset>
                        <legend><kantega:label key="aksess.multimedia.author"/></legend>
                        <input type="text" class="fullWidth" name="author" id="MultimediaAuthor" value="<c:out value="${media.author}"/>" maxlength="255" <c:if test="${!isPropertyPaneEditable}">disabled="disabled"</c:if>>
                    </fieldset>
                </div>

                <c:if test="${isPropertyPaneEditable && showDimension}">
                    <div class="sidebarFieldset">
                        <fieldset>
                            <legend><kantega:label key="aksess.multimedia.size"/></legend>
                            <label for="width"><kantega:label key="aksess.multimedia.width"/></label> <input type="text" size="5" id="width" name="width" value="<c:if test="${media.width > 0}">${media.width}</c:if>">
                            <label for="height"><kantega:label key="aksess.multimedia.height"/></label> <input type="text" size="5" id="height" name="height" value="<c:if test="${media.height > 0}">${media.height}</c:if>">
                            <c:if test="${showDimensionInfo}">
                                <div class="ui-state-highlight"><kantega:label key="aksess.multimedia.sizeinfo"/></div>
                            </c:if>
                        </fieldset>
                    </div>
                </c:if>

                <div class="sidebarFieldset">
                    <fieldset>
                        <legend><kantega:label key="aksess.multimedia.usage"/></legend>
                        <textarea name="usage" id="MultimediaUsage" rows="4" cols="20" class="fullWidth" wrap="soft" <c:if test="${!isPropertyPaneEditable}">disabled="disabled"</c:if>><c:out value="${media.usage}"/></textarea>
                    </fieldset>
                </div>
                <div class="sidebarFieldset">
                    <fieldset>
                        <legend><kantega:label key="aksess.multimedia.description"/></legend>
                        <textarea name="description" id="MultimediaDescription" rows="4" cols="20" class="fullWidth" wrap="soft" <c:if test="${!isPropertyPaneEditable}">disabled="disabled"</c:if>><c:out value="${media.description}"/></textarea>
                    </fieldset>
                </div>

                <c:if test="${not empty usages}">
                    <div class="sidebarFieldset">
                        <fieldset>
                            <legend><kantega:label key="aksess.multimedia.pages.using"/></legend>
                            <ul id="MultimediaPagesUsing">
                                <c:forEach items="${usages}" var="page">
                                    <li>
                                        <a href="${page.url}" target="_new">${page.title}</a>
                                    </li>
                                </c:forEach>
                            </ul>
                        </fieldset>
                    </div>
                </c:if>
                <c:if test="${isPropertyPaneEditable}">
            </form>
            </c:if>
        </div>

        <div id="Framesplit"></div>
        <div class="clearing"></div>

    </div>

</kantega:section>

<%@include file="commonLayout.jsp"%>