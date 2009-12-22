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
            <c:when test="${currentContent != null}">
            // User is editing a page
            $("#ModesMenu .button .view").click(function(e){
                debug("view content");
                e.preventDefault();

                gotoMode("Navigate");
            });
            $("#ModesMenu .button .edit").click(function(e){
                debug("edit content");
                e.preventDefault();
                gotoMode("SaveContent");
            });
            $("#ModesMenu .button .organize").click(function(e){
                e.preventDefault();
                gotoMode("Organize");
            });

            // These buttons are only displayed when user is editing a page
            $("#EditContentButtons input.publish").click(function(){
                saveContent(<%=ContentStatus.PUBLISHED%>);
            });
            $("#EditContentButtons input.save").click(function(){
                saveContent(<%=ContentStatus.WAITING_FOR_APPROVAL%>);
            });
            $("#EditContentButtons input.savedraft").click(function(){
                saveContent(<%=ContentStatus.DRAFT%>);
            });
            $("#EditContentButtons input.hearing").click(function(){
                saveContent(<%=ContentStatus.HEARING%>);
            });
            $("#EditContentButtons input.cancel").click(function(){
                if (confirmCancel) {
                    window.location.href = 'CancelEdit.action';
                }
            });

            // Prevent user from clicking top menu
            $("#TopMenu a").click(function (e) {
                if (!confirmCancel()) {
                    e.preventDefault();
                }
            });

            </c:when>
            <c:otherwise>
                $("#ModesMenu .button .edit").click(function(){
                    Publish.edit(getQueryParam("thisId", currentUrl));
                });
            </c:otherwise>
        </c:choose>
    }

    <c:if test="${currentContent != null}">
    function confirmCancel() {
        var confirmCancel = true;
        if (isModified()) {
            confirmCancel = confirm("Cancel changes?");
        }
        return confirmCancel;
    }
    
    function isModified() {
        var isModified = $("#IsModified").val();
        if ("true" == isModified) {
            return true;
        } else {
            return false;
        }
    }

    function gotoMode(action) {
        action = action + ".action";
        var href = "" + window.location.href;
        if (href.indexOf(action) != -1) {
            // Tried to click current tab
            return;
        }

        document.myform.elements['action'].value = action;
        saveContent("");
    }
    </c:if>    
</script>

