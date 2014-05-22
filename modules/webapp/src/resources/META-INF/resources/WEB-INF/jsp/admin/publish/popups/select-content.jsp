<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kantega" uri="http://www.kantega.no/aksess/tags/commons" %>
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
  ~ limitations under the License
  --%>

<%@ page contentType="text/html;charset=utf-8" language="java" pageEncoding="utf-8" %>

<kantega:section id="title">
    <kantega:label key="aksess.popup.selectcontent"/>
</kantega:section>

<kantega:section id="head">
    <link rel="stylesheet" type="text/css" href="<kantega:expireurl url="/admin/css/navigate.css"/>">
    <script type="text/javascript" src="<kantega:expireurl url="/admin/js/navigate.js"/>"></script>
    <script type="text/javascript" src="<kantega:expireurl url="/admin/js/editcontext.js"/>"></script>
    <script type="text/javascript">
        var currentItemIdentifier = "/content/${currentId}/dummytitle";
        var expand = true;

        $(document).ready(function() {
            openaksess.common.debug("$(document).ready(): select-content");
            openaksess.navigate.updateNavigator(currentItemIdentifier, true);
            $("#ContentSelectorTabs").tabs();
        });

        function buttonOkPressed() {
            closeWindow();
        }

        var updateTextNotification = function(title){
            var addedContent = $("#AddedContent");
            var isVisible = addedContent.is(":visible");
            openaksess.common.debug("Visible:" +  isVisible);
            if( ! isVisible){
                addedContent.removeClass("hidden");
            }
            var text = "<kantega:label key='aksess.selectcontent.notification'/>" + title;
            addedContent.html(text).fadeIn("slow");
        };

        openaksess.navigate.getNavigatorAction = function() {
            return "${pageContext.request.contextPath}/admin/publish/ContentNavigator.action";
        };

        openaksess.navigate.getNavigatorParams = function(){
            return {
                startId: '${startId}',
                highlightCurrent: false
            };
        };

        openaksess.navigate.onNavigatorTitleClick = function(elm) {
            updateParentWindowWithSelectedElement(elm);
        };

        openaksess.navigate.getItemIdentifierFromNavigatorHref = function(href) {
            return openaksess.common.getQueryParam("thisId", href);
        };

        openaksess.navigate.getCurrentItemIdentifier = function() {
            return currentItemIdentifier;
        };

        var updateParentWindowWithSelectedElement = function(elm){
            var href = elm.attr("href");
            var title = elm.attr("title");
            var id;
            var url;

            <c:choose>
                <c:when test="${selectContentId}">
                    id = openaksess.common.getQueryParam("contentId", href);
                    url = "/content.ap?contentId=" + id + "&amp;contextId=$contextId$";
                </c:when>
                <c:otherwise>
                    id = openaksess.common.getQueryParam("thisId", href);
                    url = "/content/" + id + "/" + title;
                </c:otherwise>
            </c:choose>
            updateParentWindowWithContentAttributes(id, title,url);
        };

        var autocompleteCallback = function(event, ui){
            var url;
            <c:choose>
                <c:when test="${selectContentId}">
                    url = "/content.ap?contentId=" + ui.item.id + "&amp;contextId=$contextId$";
                </c:when>
                <c:otherwise>
                    url = "/content.ap?thisId=" + ui.item.id;
                </c:otherwise>
            </c:choose>
            updateParentWindowWithContentAttributes(ui.item.id,ui.item.value,url);

        };

        var updateParentWindowWithContentAttributes = function(id,title,url){
            var w = getParent();
            if (w) {
                if (w.openaksess.editcontext.doInsertTag) {
                    w.openaksess.editcontext.insertValueIntoForm(url);
                } else {
                    w.openaksess.editcontext.insertValueAndNameIntoForm(id, title);
                }
            }
            <c:choose>
                <c:when test="${multiple}">
                    updateTextNotification(title);
                </c:when>
                <c:otherwise>
                    closeWindow();
                </c:otherwise>
            </c:choose>
        }
    </script>
</kantega:section>

<kantega:section id="body">
    <div id="SelectPage">

        <c:if test="${multiple}">
            <div id="AddedContent" class="hidden ui-state-highlight"></div>
        </c:if>
        <div id="ContentSelectorTabs">
            <ul>
                <li><a href="#Navigation"><kantega:label key="aksess.selectcontent.navigate"/></a></li>
                <li><a href="#Search"><kantega:label key="aksess.search.title"/></a></li>

            </ul>
            <div id="Navigation">
                <div id="Navigator"></div>
            </div>
            <div id="Search">
                <div class="formElement">
                    <div class="heading"><label><kantega:label key="aksess.selectcontent.search"/></label></div>
                    <div class="inputs">
                        <div id="AssociationId">
                            <input type="hidden" name="url_associationId" id="url_associationId" value="">
                            <input type="text" name="url_associationIdtext" id="url_associationIdtext" onfocus="this.select()" value="<kantega:label key="aksess.selectcontent.search.hint"/>" class="fullWidth" maxlength="128">
                            <script type="text/javascript">
                                $(document).ready(function() {
                                    $("#url_associationIdtext").oaAutocomplete({
                                        defaultValue: '<kantega:label key="aksess.selectcontent.search.hint"/>',
                                        source: "${pageContext.request.contextPath}/ajax/AutocompleteContent.action<c:if test="${selectContentId}">?useContentId=true</c:if>",
                                        select: autocompleteCallback
                                    });
                                });
                            </script>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="buttonGroup">
            <span class="button"><input type="button" class="ok" value="<kantega:label key="aksess.button.closewindow"/>"></span>
        </div>
    </div>
</kantega:section>


<%@include file="../../layout/popupLayout.jsp"%>