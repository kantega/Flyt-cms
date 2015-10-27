<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>
<%@ taglib uri="http://www.kantega.no/aksess/tags/admin" prefix="admin" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
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
        properties.contextPath = '${pageContext.request.contextPath}';
    </script>
    <script type="text/javascript" src="<kantega:expireurl url="/aksess/js/aksess-i18n.jjs"/>"></script>
    <script type="text/javascript" src="<kantega:expireurl url="/wro-oa/admin-editcontentlayout.js"/>"></script>

    <script type="text/javascript" src="<kantega:expireurl url="/flytcms/tinymce/tinymce.min.js"/>"></script>

    <%@include file="fragments/publishModesAndButtonsJS.jsp"%>

    <script type="text/javascript">
        var hasSubmitted = false;
        var lastSavedForm;
        $(document).ready(function(){
            bindToolbarButtons();
            <c:choose>
            <c:when test="${scrollTo != null}">
            // This fails if a delay is not added
            setTimeout(scrollTo, 500);
            </c:when>
            <c:when test="${errors != null && errors.length > 0}">
            // Error, do nothing
            </c:when>
            <c:otherwise>
            // Set focus to first attribute if it is a input field
            $("#EditContentForm").find(".contentAttribute:first input[type='text']").focus();
            </c:otherwise>
            </c:choose>

            openaksess.editcontext.init();
            initShadowdraft();
        });

        function scrollTo() {
            var top = $("#${scrollTo} .contentAttributeRepeaterRow:last").offset().top;
            if (top > 100) {
                top -= 100;
            } else {
                top = 0;
            }
            $("#EditContentMain").scrollTop(top);
        }

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

        function initShadowdraft(){
            openaksess.editcontext.saveAll();
            lastSavedForm = $("#EditContentForm").serializeArray();
        }

        function submitShadowdraft(){
            var form = $("#EditContentForm");
            //submit her
            form.submit(function( event ) {
                lastSavedForm = $( this ).serializeArray();
                $.post( form.attr("action"), lastSavedForm );
                event.preventDefault();
            });
            form.submit();
            form.unbind("submit");
        }

        function zeropad(input){
            if(input < 10){
                return '0' + input;
            } else {
                return input;
            }
        }

        function displaySaveTimestamp(){
            var date = new Date;
            var timeString = zeropad(date.getHours()) + ":" + zeropad(date.getMinutes()) + ":" + zeropad(date.getSeconds());
            $("#lastAutosaveTime").text(timeString);
            $("#lastAutosaveSpan").show();
            $("#minorChangeSpan").addClass("barButton");
        }

        function saveContentBackgroundDraft(){
            var status ="<%=ContentStatus.GHOSTDRAFT.getTypeAsInt()%>"; //21
            openaksess.common.debug("publishLayout.saveContentBackgroundDraft(): status: " + status);
            if (validatePublishProperties()) {
                window.onbeforeunload = null;
                openaksess.editcontext.saveAll();
                var $contentIsModified = $("#ContentIsModified");
                if ($contentIsModified.val() == "false") {
                    $contentIsModified.val(openaksess.editcontext.isModified());
                }
                $("#ContentStatus").val(status);
                submitShadowdraft();
                displaySaveTimestamp();
            }
        }

        function saveContent(status) {
            openaksess.common.debug("publishLayout.saveContent(): status: " + status);
            if (validatePublishProperties()) {
                if (!hasSubmitted) {
                    window.onbeforeunload = null;
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

        function confirmBeforeUnload() {
            if (openaksess.editcontext.isModified()) {
                return '<kantega:label key="aksess.editcontent.navigateaway"/>';
            } else {
                return '';
            }
        }

        function formIsChanged(form){
            // Satse på at samme type og length
            for(var i=0; i< form.length; i++){
                if( JSON.stringify(form[i]) != JSON.stringify(lastSavedForm[i]) ){
                    return true;
                }
            }
            return false;
        }
        function autosaveMethod(){
            openaksess.editcontext.saveAll(); // Saves tinyMCE data to textareas
            var currentForm = $("#EditContentForm").serializeArray();
            if( formIsChanged(currentForm) ){
                openaksess.common.debug("editContentLayout.saveBackgroundDraft");
                saveContentBackgroundDraft();
            }
        }

        window.onbeforeunload = confirmBeforeUnload;
        window.setInterval( autosaveMethod, (5 *1000));
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
        <c:if test="${canEditContentMetadata}">
            <a href="#" class="tab<c:if test="${metadataActive}"> active</c:if>"><span><span class="metadata"><kantega:label key="aksess.tools.metadata"/></span></span></a>
        </c:if>
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
            <div id="Framesplit" class="framesplit metadata"></div>
            <div class="clearing"></div>
        </div>
        <input type="hidden" id="ContentStatus" name="status" value="">
        <input type="hidden" name="action" value="">
        <input type="hidden" id="AddRepeaterRow" name="addRepeaterRow" value="">
        <input type="hidden" id="DeleteRepeaterRow" name="deleteRepeaterRow" value="">
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
