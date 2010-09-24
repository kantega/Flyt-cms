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
    <link rel="stylesheet" type="text/css" href="<kantega:expireurl url="/wro-oa/admin-editcontentlayout.css"/>">
    <script type="text/javascript">
        if (typeof properties == 'undefined') {
            var properties = { };
        }
        if (typeof properties.date == 'undefined') {
            properties.date = { };
        }
        if (typeof properties.editcontext == 'undefined') {
            properties.editcontext = { };
        }
        if (typeof properties.formeditor == 'undefined') {
            properties.formeditor = { };
        }
        properties.date.labels = {
            feilformat : "<kantega:label key="aksess.js.advarsel.dato.feilformat" escapeJavascript="true"/>",
            skilletegn : "<kantega:label key="aksess.js.advarsel.dato.skilletegn" escapeJavascript="true"/>",
            feildag : "<kantega:label key="aksess.js.advarsel.dato.feildag" escapeJavascript="true"/>",
            feilmaned : "<kantega:label key="aksess.js.advarsel.dato.feilmaned" escapeJavascript="true"/>",
            feilar : "<kantega:label key="aksess.js.advarsel.dato.feilar" escapeJavascript="true"/>",
            feildagtall : "<kantega:label key="aksess.js.advarsel.dato.feildagtall" escapeJavascript="true"/>",
            feilmanedtall : "<kantega:label key="aksess.js.advarsel.dato.feilmanedtall" escapeJavascript="true"/>",
            feilartall : "<kantega:label key="aksess.js.advarsel.dato.feilartall" escapeJavascript="true"/>",
            feilskuddarmaned : "<kantega:label key="aksess.js.advarsel.dato.feilskuddarmaned" escapeJavascript="true"/>",
            feiltidsformatKolon : "<kantega:label key="aksess.js.advarsel.dato.feiltidsformat.kolon" escapeJavascript="true"/>",
            feiltidsformat : "<kantega:label key="aksess.js.advarsel.dato.feiltidsformat" escapeJavascript="true"/>",
            feiltidsformatMinuttermindre : "<kantega:label key="aksess.js.advarsel.dato.feiltidsformat.minuttermindre" escapeJavascript="true"/>",
            feiltidsformatMinutterstorre : "<kantega:label key="aksess.js.advarsel.dato.feiltidsformat.minutterstorre" escapeJavascript="true"/>",
            feiltidsformatTimermindre : "<kantega:label key="aksess.js.advarsel.dato.feiltidsformat.timermindre" escapeJavascript="true"/>",
            feiltidsformatTimerstorre : "<kantega:label key="aksess.js.advarsel.dato.feiltidsformat.timerstorre" escapeJavascript="true"/>"
        };
        properties.editcontext.labels = {
            selecttopic : '<kantega:label key="aksess.selecttopic.title" escapeJavascript="true"/>',
            selectcontent : '<kantega:label key="aksess.popup.selectcontent" escapeJavascript="true"/>',
            selectorgunit : '<kantega:label key="aksess.popup.selectorgunit" escapeJavascript="true"/>',
            warningMaxchoose : '<kantega:label key="aksess.js.advarsel.dukanmaksimaltvelge" escapeJavascript="true"/> ',
            warningElements : '<kantega:label key="aksess.js.advarsel.elementer" escapeJavascript="true"/>',
            adduser : '<kantega:label key="aksess.adduser.title" escapeJavascript="true"/>',
            multimedia : '<kantega:label key="aksess.multimedia.title" escapeJavascript="true"/>',
            addrole : '<kantega:label key="aksess.addrole.title" escapeJavascript="true"/>',
            editablelistValue : '<kantega:label key="aksess.editablelist.value" escapeJavascript="true"/>'
        };
        properties.formeditor.labels = {
            buttonEdit : '<kantega:label key="aksess.button.edit" escapeJavascript="true"/>',
            buttonDelete : '<kantega:label key="aksess.button.delete" escapeJavascript="true"/>',
            deleteformdataConfirm : '<kantega:label key="aksess.formeditor.deleteformdata.confirm" escapeJavascript="true"/>',
            typeText : '<kantega:label key="aksess.formeditor.type.text" escapeJavascript="true"/>',
            typeTextarea : '<kantega:label key="aksess.formeditor.type.textarea" escapeJavascript="true"/>',
            typeCheckbox : '<kantega:label key="aksess.formeditor.type.checkbox" escapeJavascript="true"/>',
            typeRadio : '<kantega:label key="aksess.formeditor.type.radio" escapeJavascript="true"/>',
            typeSelect : '<kantega:label key="aksess.formeditor.type.select" escapeJavascript="true"/>',
            typeHidden : '<kantega:label key="aksess.formeditor.type.hidden" escapeJavascript="true"/>'
        };
        properties.contextPath = '${pageContext.request.contextPath}';
    </script>
    <script type="text/javascript" src="<kantega:expireurl url="/wro-oa/admin-editcontentlayout.js"/>"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/aksess/tiny_mce/tiny_mce_gzip.js"></script>

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
            openaksess.common.debug("publishLayout.saveContent(): status: " + status);

            if (validatePublishProperties()) {
                if (!hasSubmitted) {
                    hasSubmitted = true;
                    openaksess.editcontext.saveAll();
                    var $contentIsModified = $("#ContentIsModified");
                    if ($contentIsModified.val() == "false") {
                        $contentIsModified.val(openaksess.editcontext.isModified());
                    }
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

<c:set var="hideSearch" value="true"/>

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

    <form name="activeversion" action="UseVersion.action" method="post">
        <input type="hidden" name="version" value="-1">
    </form>
    
    <form name="deleteversion" action="DeleteVersion.action" method="post">
        <input type="hidden" name="version" value="-1">
    </form>

</kantega:section>

<%@include file="commonLayout.jsp"%>