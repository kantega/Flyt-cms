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
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/admin/css/publish.css">
    <script type="text/javascript" src="../js/publish.jjs"></script>
    <script type="text/javascript" src="../js/browserdetect.js"></script>
    <script type="text/javascript" src="../js/date.jsp"></script>
    <script type="text/javascript" src="../js/edit.jjs"></script>
    <script type="text/javascript" src="../../aksess/js/autocomplete.js"></script>
    <script type="text/javascript" src="../../aksess/tiny_mce/tiny_mce.js"></script>

    <%@include file="fragments/publishModesAndButtonsJS.jsp"%>

    <script type="text/javascript">
        var hasSubmitted = false;

        $(document).ready(function(){
            bindToolbarButtons();
            // Set focus to first input field
            $("#EditContentForm input[type='text']:first").focus();
        });

        function bindToolbarButtons() {
        <c:if test="${!contentActive}">
            $("#TabToolsMenu .tab .content").click(function(){
                gotoMode("SaveContent");
            });
        </c:if>
        <c:if test="${!metadataActive}">
            $("#TabToolsMenu .tab .metadata").click(function(){
                gotoMode("SaveMetadata");
            });
        </c:if>
        <c:if test="${!versionsActive}">
            $("#TabToolsMenu .tab .versions").click(function(){
                gotoMode("SaveVersion");
            });
        </c:if>
        <c:if test="${!attachmentsActive}">
            $("#TabToolsMenu .tab .attachments").click(function(){
                gotoMode("SaveAttachments");
            });
        </c:if>
        }

        function saveContent(status) {
            debug("publishLayout.saveContent(): status: " + status);

            if (validatePublishProperties()) {
                if (!hasSubmitted) {
                    hasSubmitted = true;
                    $("#ContentIsModified").val(isModified());
                    $("#ContentStatus").val(status);
                    document.myform.submit();
                }
            }
        }

    </script>
</kantega:section>

<kantega:section id="topMenu">
    <%@include file="fragments/topMenu.jsp"%>
</kantega:section>

<kantega:section id="modesMenu">
    <%@include file="fragments/publishModesMenu.jsp"%>
</kantega:section>

<kantega:section id="tabToolsMenu">
    <div class="tabGroup">
        <a href="#" class="tab<c:if test="${contentActive}"> active</c:if>"><span><span class="content"><kantega:label key="aksess.tools.content"/></span></span></a>
        <a href="#" class="tab<c:if test="${metadataActive}"> active</c:if>"><span><span class="metadata"><kantega:label key="aksess.tools.metadata"/></span></span></a>
        <a href="#" class="tab<c:if test="${attachmentsActive}"> active</c:if>"><span><span class="attachments"><kantega:label key="aksess.tools.attachments"/></span></span></a>
        <a href="#" class="tab<c:if test="${versionsActive}"> active</c:if>"><span><span class="versions"><kantega:label key="aksess.tools.versions"/></span></span></a>
    </div>
</kantega:section>

<kantega:section id="body">
    <form name="myform" id="EditContentForm" action="" method="post" enctype="multipart/form-data">

        <div id="Content" class="publish">
            <div id="MainPane">
                <div id="EditContentMain">
                    <div id="EditContentPane">
                        <kantega:getsection id="content"/>
                    </div>
                </div>
                <div id="EditContentButtons" class="buttonBar">
                    <%@include file="fragments/editContentButtons.jsp"%>
                </div>
            </div>
            <div id="SideBar">
                <%@ include file="../publish/fragments/publishproperties.jsp" %>
            </div>
            <div id="Framesplit"></div>
            <div class="clearing"></div>
        </div>
        <input type="hidden" id="ContentStatus" name="status" value="">
        <input type="hidden" name="action" value="">
        <input type="hidden" name="currentId" value="${currentContent.id}">
        <input type="hidden" id="ContentIsModified" name="isModified" value="${currentContent.modified}">
    </form>

</kantega:section>

<%@include file="commonLayout.jsp"%>