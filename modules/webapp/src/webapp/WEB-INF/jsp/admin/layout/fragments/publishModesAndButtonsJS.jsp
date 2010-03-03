<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="iso-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="no.kantega.publishing.common.data.enums.ContentStatus" %>
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
<script type="text/javascript">
    $(document).ready(function(){
        bindPublishButtons();
    });

    function bindPublishButtons() {
        <c:choose>
            <c:when test="${hasUnsavedChanges || isEditing}">
            // User is editing a page and it is changed
            $("#ModesMenu .button .view").click(function(e){
                debug("publishModesAndButtonsJS.view");
                e.preventDefault();
                gotoMode("Navigate");
            });
            $("#ModesMenu .button .edit").click(function(e){
                debug("publishModesAndButtonsJS.edit");
                e.preventDefault();
                gotoMode("SaveContent");
            });
            $("#ModesMenu .button .organize").click(function(e){
                debug("publishModesAndButtonsJS.organize");
                e.preventDefault();
                gotoMode("Organize");
            });
            $("#ModesMenu .button .linkcheck").click(function(e){
                debug("publishModesAndButtonsJS.linkcheck");
                e.preventDefault();
                gotoMode("LinkCheck");
            });
            $("#ModesMenu .button .statistics").click(function(e){
                debug("publishModesAndButtonsJS.statistics");
                e.preventDefault();
                gotoMode("Statistics");
            });
            $("#ModesMenu .button .notes").click(function(e){
                debug("publishModesAndButtonsJS.notes");
                e.preventDefault();
                gotoMode("Notes");
            });

            // Prevent user from clicking top menu
            $("#TopMenu a").click(function (e) {
                debug("publishModesAndButtonsJS: topmenu click");
                if (!confirmCancel()) {
                    e.preventDefault();
                }
            });

            </c:when>
            <c:when test="${!hasUnsavedChanges && !isEditing}">
                // No unsaved changes and user is not editing
                $("#ModesMenu .button .edit").click(function(){
                    Publish.edit(getQueryParam("thisId", stateHandler.getState()));
                });
            </c:when>
        </c:choose>

        <c:if test="${hasUnsavedChanges || isEditing}">
        // These buttons are only displayed when user is editing a page or previewing with a changed page
        $("#EditContentButtons input.publish").click(function(){
            debug("publishModesAndButtonsJS.publish");
            saveContent(<%=ContentStatus.PUBLISHED%>);
        });
        $("#EditContentButtons input.save").click(function(){
            debug("publishModesAndButtonsJS.save");
            saveContent(<%=ContentStatus.WAITING_FOR_APPROVAL%>);
        });
        $("#EditContentButtons input.savedraft").click(function(){
            debug("publishModesAndButtonsJS.savedraft");
            saveContent(<%=ContentStatus.DRAFT%>);
        });
        $("#EditContentButtons input.hearing").click(function(){
            debug("publishModesAndButtonsJS.hearing");
            saveContent(<%=ContentStatus.HEARING%>);
        });
        $("#EditContentButtons input.cancel").click(function(){
            debug("publishModesAndButtonsJS.cancel");
            if (confirmCancel) {
                window.location.href = 'CancelEdit.action';
            }
        });
        </c:if>
    }

    function confirmCancel() {
        var confirmCancel = true;
        if (isPageModified()) {
            confirmCancel = confirm("Cancel changes?");
        }
        return confirmCancel;
    }
    
    function isPageModified() {
        return "true" == $("#IsModified").val();
    }

    function gotoMode(action) {
        debug("publishModesAndButtonsJS.gotoMode(): action: "+action);
        action = action + ".action";
        var href = "" + window.location.href;
        if (href.indexOf(action) != -1) {
            // Tried to click current tab
            debug("publishModesAndButtonsJS.gotoMode(): Tried to click current tab");
            return;
        }

        document.myform.elements['action'].value = action;
        saveContent("");
    }
</script>

